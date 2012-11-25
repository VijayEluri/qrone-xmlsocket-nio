package org.qrone.sample;

import java.io.IOException;
import java.net.UnknownHostException;

import org.qrone.xmlsocket.XMLSocket;
import org.qrone.xmlsocket.XMLSocketThread;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.w3c.dom.Document;

/**
 * �����Ă��� XML ��ڑ����̑S���ɂ��̂܂ܑ���T�[�o�[�iport:9601�j�̃T���v���B
 * 
 * @author J.Tabuchi
 * @since 2005/8/6
 * @version 1.0
 * @link QrONE Technology : http://www.qrone.org/
 */
public class QrXMLSocketClientTest {
	// �T�[�o�[�̑҂������|�[�g�ԍ�
	public static final int SERVER_PORT = 9601;
	
	public static void main(String[] args){
		int count=1;
		XMLSocketThread st = null;
		try {
			st = new XMLSocketThread();
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		for(;count<2;count++){
			final int clientnumber = count;
			final XMLSocket socket = new XMLSocket(st);
			// �N���C�A���g�̃C�x���g�n���h���̓o�^
			socket.addXMLSocketListener(new XMLSocketListener(){
				//�@�ڑ��J�n��
				public void onConnect(boolean success) {
					System.out.println("flash:"+clientnumber+":connect:");
	
					socket.send("<?xml version=\"1.0\" encoding=\"Shift_JIS\"?>"+
								"<Message date=\"�e�X�g\"/>");
				}
				
				// �ڑ��I����
				public void onClose() {
					System.out.println("flash:"+clientnumber+":close:");
				}
	
				// �G���[
				public void onError(Exception e) {
					e.printStackTrace();
				}
	
				//�@�^�C���A�E�g
				public void onTimeout() {
					System.out.println("flash:"+clientnumber+":timeout");
				}
	
				// Flash ����̃f�[�^��M
				public void onData(String data) {
					System.out.println("flash:"+clientnumber+":data:"+data);
				}
			});
			
			// �T�[�o�[���J�n����
			try {
				socket.connect("localhost",9601);
				Thread.sleep(10);
			} catch (UnknownHostException e1) {
				e1.printStackTrace();
			} catch (IOException e1) {
				e1.printStackTrace();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}
