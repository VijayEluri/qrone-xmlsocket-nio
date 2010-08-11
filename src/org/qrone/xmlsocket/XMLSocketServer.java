package org.qrone.xmlsocket;

import java.io.IOException;
import java.net.ServerSocket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import org.apache.log4j.Logger;
import org.qrone.xmlsocket.event.XMLSocketServerListener;
import org.qrone.xmlsocket.nio.SelectorServerSocket;

public class XMLSocketServer extends SelectorServerSocket{
	private static final Logger log = Logger.getLogger(XMLSocketServer.class);
	
	public XMLSocketServer() throws IOException {
		super(new XMLSocketThread());
	}
	public XMLSocketServer(XMLSocketThread thread) {
		super(thread);
	}

	private static final int SERVER_TIMEOUT = 30000;
	private LinkedList serverlistener = new LinkedList();

	private Charset inputcs  = Charset.forName("UTF-8");
	private Charset outputcs = Charset.forName("UTF-8");

	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̃G���R�[�f�B���O���w�肵�܂��B
	 * �w�肵�Ȃ��ꍇ�W���ł� UTF-8 ���ݒ肳��Ă��܂����A�ʏ�� Flash �Ɠ��{��ŒʐM����ɂ�
	 * ShiftJIS �ł���K�v������܂��B
	 * 
	 * @param charset ���o�͂̕����R�[�h���w��
	 */
	public void setEncoding(Charset cs){
		setEncoding(cs,cs);
	}

	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̃G���R�[�f�B���O���w�肵�܂��B
	 * 
	 * @see #setEncoding(Charset);
	 * @param charset ���o�͂̕����R�[�h���w��
	 */
	public void setEncoding(String charset){
		setEncoding(Charset.forName(charset));
	}

	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̃G���R�[�f�B���O���w�肵�܂��B
	 * 
	 * @see #setEncoding(Charset);
	 * @param input ���͂̕����R�[�h���w��
	 * @param output �o�͂̕����R�[�h���w��
	 */
	public void setEncoding(Charset input, Charset output){
		inputcs = input;
		outputcs = output;
	}

	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̃G���R�[�f�B���O���w�肵�܂��B
	 * 
	 * @see #setEncoding(Charset);
	 * @param input ���͂̕����R�[�h���w��
	 * @param output �o�͂̕����R�[�h���w��
	 */
	public void setEncoding(String input, String output){
		setEncoding(Charset.forName(input),Charset.forName(output));
	}

	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̓��̓G���R�[�f�B���O���擾���܂��B
	 */
	public Charset getInputEncoding(){
		return inputcs;
	}
	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̏o�̓G���R�[�f�B���O���擾���܂��B
	 */
	public Charset getOutputEncoding(){
		return outputcs;
	}
	
	/**
	 * XMLSocketServer�@�T�[�o�[�Ɋ֘A�Â����Ă��� ServerSocket �C���X�^���X��Ԃ��܂��B
	 * @return ���p���̃T�[�o�[�\�P�b�g
	 */
	public ServerSocket getServerSocket(){
		return serverchannel.socket();
	}
	
	/**
	 * �T�[�o�[�J�n����ɌĂяo����܂��B success == false �̎��ɂ�
	 * <b>�T�[�o�[���J�n����Ă��܂���B</b><BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) 
	 * �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 * @param success �T�[�o�[�J�n����
	 */
	public void onOpen(boolean success){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onOpen(success);
		}
 		if(success) log.debug("OPEN");
	}

	/**
	 * �G���[����ɌĂяo����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) 
	 * �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 * @param e �G���[
	 */
	public void onError(Exception e) {
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onError(e);
		}
 		log.debug("ERROR");
	}

	/**
	 * �T�[�o�[�I������ɌĂяo����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) 
	 * �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 */
	public void onClose(){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onClose();
		}
 		log.debug("CLOSE");
	}

	/**
	 * �V���� Macromedia Flash �� .swf �t�@�C������ XMLSocket �ʐM��v�����ꂽ����
	 * �Ăяo����Aswf �t�@�C���ƒʐM���m�����钼�O�� XMLSocket �I�u�W�F�N�g���n����܂��B<BR>
	 * �p�������N���X�ł��̃��\�b�h���I�[�o�[���C�h����ƃC�x���g�n���h���̃C�x���g���Ă΂�Ȃ��Ȃ�܂��B<BR>
	 * <BR>
	 * �ʏ�� addXMLSocketServerListener(XMLSocketServerListener) 
	 * �ŃC�x���g�n���h���𗘗p���Ă��������B
	 * @see #addXMLSocketServerListener(XMLSocketServerListener)
	 */
	public void onNewClient(XMLSocket socket){
 		for (Iterator iter = serverlistener.iterator(); iter.hasNext();) {
			((XMLSocketServerListener)iter.next()).onNewClient(socket);
		}
 		log.debug("NEWCLIENT " 
 				+ socket.getSocket().getInetAddress().getHostAddress());
	}
	
	/**
	 * �C�x���g�n���h����o�^���Ċe��C�x���g���擾���܂��B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void addXMLSocketServerListener(XMLSocketServerListener listener) {
		serverlistener.add(listener);
	}

	/**
	 * �o�^����Ă���C�x���g�n���h�����폜���܂��B
	 * @param listener�@�C�x���g�n���h��
	 */
	public void removeXMLSocketServerListener(XMLSocketServerListener listener) {
		serverlistener.remove(listener);
	}
}
