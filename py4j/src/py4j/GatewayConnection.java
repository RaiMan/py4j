/**
 * Copyright (c) 2009, Barthelemy Dagenais All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * 
 * - Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 * 
 * - Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * 
 * - The name of the author may not be used to endorse or promote products
 * derived from this software without specific prior written permission.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package py4j;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class GatewayConnection implements Runnable {

	private final Gateway gateway;
	private final Socket socket;
	private final BufferedWriter writer;
	private final BufferedReader reader;
	private final Map<String,Command> commands;
	
	public GatewayConnection(Gateway gateway, Socket socket) throws IOException {
		super();
		this.gateway = gateway;
		this.socket = socket;
		this.reader = new BufferedReader(new InputStreamReader(socket.getInputStream(), Charset.forName("UTF-8")));
		this.writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), Charset.forName("UTF-8")));
		this.commands = new HashMap<String,Command>();
		Thread t = new Thread(this);
		t.start();
	}
	
	protected void initCommands(Gateway gateway) {
		Command gatewayCommand = new GatewayCommand();
		Command callCommand = new CallCommand();
		gatewayCommand.init(gateway);
		callCommand.init(gateway);
		commands.put("g",gatewayCommand);
		commands.put("c",callCommand);
	}
	
	
	@Override
	public void run() {
		try {
			String command = null;
			do {
				command = reader.readLine();
				
			} while (command != null && !command.equals('q'));
			writer.close();
			reader.close();
			socket.close();
		} catch(Exception e) {
			throw new Py4JException(e);
		}
	}

}
