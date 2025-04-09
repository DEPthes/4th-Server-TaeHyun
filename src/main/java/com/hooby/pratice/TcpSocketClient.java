package com.hooby.pratice;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class TcpSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(TcpSocketClient.class);

    // TCP 3-Way Hand Shaking 을 위해선 Dest IP 와 Dest Port 가 필요
    private final String serverName; // 서버 이름 or IP 주소
    private final int serverPort; // 서버 포트

    // Constructor
    public TcpSocketClient(String serverName, int serverPort) {
        this.serverName = serverName;
        this.serverPort = serverPort;
    }

    public void start() throws IOException{
        try (Socket clientSocket = new Socket(serverName, serverPort);
             // 서버로 데이터를 보내기 위한 Output Stream
             DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());

             // 서버로부터 Response 데이터를 받아온다.
             BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));

             // 사용자로부터 Message 입력을 받는다.
             BufferedReader userInput = new BufferedReader(new InputStreamReader(System.in))
        ){
            // 연결된 클라이언트 객체의 정보를 띄워줌
            logger.info("🟢 Client connected: {}", clientSocket.getInetAddress());

            System.out.print("🟢 Input lowercase message: ");
            String message = userInput.readLine();

            // Network Layer 에서는 Byte Stream 으로 데이터를 보내기에, Byte 로 바꿔서 보내준다.
            outToServer.writeBytes(message + '\n');

            String modifiedSentence = inFromServer.readLine();
            System.out.println("🟢 From Server: " + modifiedSentence);

        } catch (IOException e){
            logger.error("⚠️ IO 에러가 발생했습니다.", e);
        } catch (Exception e){
            logger.error("⚠️ 예기치 못한 에러가 발생했습니다.", e);
        }
    }

    public static void main(String[] args) throws IOException {
        String serverName = "localhost";
        int serverPort = 8080;

        TcpSocketClient client = new TcpSocketClient(serverName, serverPort);
        client.start();
    }
}

/* 💡Descriptions
 *
 *   본 코드는, 네트워크 (하향식 접근) 8th 원서 내용을 기반으로 작성되었습니다.
 *   2.7 Socket Programming: Creating Network Applications (p.182)
 *
 *   🧐 왜 clientSocket.close() 가 없죠? try-with-resources 를 사용해서 객체가 자동으로 닫힘
 *
 * */