package org.qrone.sample;

import java.io.IOException;
import java.util.LinkedList;

import org.qrone.xmlsocket.XMLSocket;
import org.qrone.xmlsocket.XMLSocketServer;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.qrone.xmlsocket.event.XMLSocketServerListener;
import org.w3c.dom.Document;

/**
 * �����Ă��� XML ��ڑ����̑S���ɂ��̂܂ܑ���T�[�o�[�iport:9601�j�̃T���v���B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class QrXMLSocketServer {
	// �T�[�o�[�̑҂������|�[�g�ԍ�
	public static final int SERVER_PORT = 9601;
	
	public static void main(String[] args){
		// �ڑ����̃N���C�A���g�̃��X�g
		final LinkedList<XMLSocket> clientList = new LinkedList();
		
		// XMLSocketServer �̍쐬
		XMLSocketServer socketServer = null;
		try {
			socketServer = new XMLSocketServer();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		socketServer.setEncoding("UTF-8");
		
		// �T�[�o�[�̃C�x���g�n���h���̓o�^
		socketServer.addXMLSocketServerListener(new XMLSocketServerListener(){

			// �T�[�o�[�J�n��
			public void onOpen(boolean success) {
				System.out.println("open:" + success);
			}

			// �T�[�o�[�I��
			public void onClose() {
				System.out.println("close:");
			}

			// �G���[
			public void onError(Exception e) {
				//e.printStackTrace();
			}

			// �V�����N���C�A���g�̐ڑ�
			public void onNewClient(final XMLSocket socket) {
				// �N���C�A���g�����X�g�ɓo�^
				clientList.add(socket);
				// �N���C�A���g�̒ʂ��ԍ������
				final int clientnumber = clientList.size();
				
				System.out.println("newclient:" + clientnumber);
				
				// �N���C�A���g�̃C�x���g�n���h���̓o�^
				socket.addXMLSocketListener(new XMLSocketListener(){
					//�@�ڑ��J�n��
					public void onConnect(boolean success) {
						System.out.println("flash:"+clientnumber+":connect:");
					}
					
					// �ڑ��I����
					public void onClose() {
						System.out.println("flash:"+clientnumber+":close:");
					}

					// �G���[
					public void onError(Exception e) {
						//e.printStackTrace();
					}

					//�@�^�C���A�E�g
					public void onTimeout() {
						System.out.println("flash:"+clientnumber+":timeout");
					}

					// Flash ����̃f�[�^��M
					public void onData(String data) {
						System.out.println("flash:"+clientnumber+":data:"+data);
						socket.send("<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>"+
									"<Message date=\"�e�X�g\"/>");
					}

					// Flash �����M�����f�[�^�� XML DOM
					public void onXML(Document doc) {
					}
				});
			}
		});
		
		// �T�[�o�[���J�n����
		socketServer.open(SERVER_PORT);
	}
}
