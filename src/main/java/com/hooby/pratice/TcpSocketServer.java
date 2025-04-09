package com.hooby.pratice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class TcpSocketServer {

    private static final Logger logger = LoggerFactory.getLogger(TcpSocketServer.class);

    // TCP 3-Way Hand Shaking 을 위해선 Dest IP 와 Dest Port 가 필요
    private final int serverPort;

    // Constructor
    public TcpSocketServer(int serverPort) {
        this.serverPort = serverPort;
    }

    public void start() throws IOException {
        try(ServerSocket serverSocket = new ServerSocket(serverPort)){
            logger.info("🟢 The server is ready to receive on port {}", serverPort);

            // Ref 1
            while(true) {
                try(Socket connectionSocket = serverSocket.accept()){
                    // 사용자의 메시지를 받을 객체 생성
                    BufferedReader inFromClient = new BufferedReader(
                        new InputStreamReader(connectionSocket.getInputStream())
                    );

                    // 클라이언트로 내보낼 객체 생성
                    DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());

                    // 연결된 클라이언트 객체의 정보를 띄워줌
                    logger.info("🟢 Client connected: {}", connectionSocket.getInetAddress());

                    String message = inFromClient.readLine();
                    logger.info("🟢 Received from client: {}", message);

                    String capitalizedSentence = message.toUpperCase() + '\n';
                    outToClient.writeBytes(capitalizedSentence);
                    logger.info("🟢 Sent to client: {}", capitalizedSentence);

                } catch (IOException e){
                    logger.error("⚠️ IO 에러가 발생했어요.", e);
                } catch (Exception e){
                    logger.error("⚠️ 예기치 못한 에러가 발생했어요.", e);
                }
            }
        }
    }

    public static void main(String[] args) throws IOException{
        int serverPort = 12000;
        TcpSocketServer server = new TcpSocketServer(serverPort);
        server.start();
    }
}

/* 💡Descriptions
*
*   Ref 1::
*   여기서 중요한건 serverSocket 의 존재와 connectionSocket 의 존재이다.
*   Client 가 요청을 하면 냅다 자신의 Socket 을 내주는게 아니라, TCP 연결을 위한 Socket 으로 요청을 식별한다.
*   TCP 3-Way HandShaking 과정에서 Client 의 TCP 연결 요청을 받게 될 Socket 이 바로 serverSocket 이다.
*   TCP 연결이 되면 (accept 되면) 이제 자신의 Socket 을 내주게 되는데, 그게 바로 connectionSocket 이다.
*   물론 UDP는 그런거 없다.
*
* */