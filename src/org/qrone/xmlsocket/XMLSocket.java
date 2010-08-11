package org.qrone.xmlsocket;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.LinkedList;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.qrone.XMLTools;
import org.qrone.xmlsocket.event.XMLSocketListener;
import org.qrone.xmlsocket.inner.XMLSocketProtocolDecoder;
import org.qrone.xmlsocket.inner.XMLSocketProtocolEncoder;
import org.qrone.xmlsocket.nio.ExceptionListener;
import org.qrone.xmlsocket.nio.SelectorSocket;
import org.qrone.xmlsocket.nio.SelectorThread;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * <br>
 * �V���O���X���b�h�ʐM�͋ɂ߂đ����ʐM�Z�b�V�����ł����Ȃ����삵�A�}���`�X���b�h�����ɂ߂č�
 * ���������x�������ł��܂��B�X���b�h���@�Q�`�T�O���x�������U�蕪���邱�Ƃ��l�����܂����ʐM�Ɋ�
 * ���Ắ@�P�X���b�h�ŁA�Q�O�O�O�`�T�O�O�O�߂��ʐM�Z�b�V��������Ȃ������ł��܂�<br>
 * <br>
 * �������X���b�h����Ă΂�� onData �� onXML �Ƃ������֐��Œ������Ԃ̂����鏈���A�Ⴆ��
 * �t�@�C�����o�͓��̃u���b�N�𔺂��������s���ƁA���̏������S�ʐM�Z�b�V�������X�g�b�v���Ă��܂��܂��B
 * �܂�����Ȍv�Z�����Ȃǂ����ƂȂ蓾�܂��B<br>
 * 
 * @author Administrator
 */
public class XMLSocket extends SelectorSocket{
	private static final Logger log = Logger.getLogger(XMLSocket.class);

	/**
	 * XMLSocket�@�ʐM�I�u�W�F�N�g�𐶐����܂��B��������V�����I�u�W�F�N�g�������ĐV�����ʐM���s��
	 * ���Ƃ��ł��܂��B���������̃R���X�g���N�^�ł͐V���� XMLSocketThread �𐶐�����ׁB����
	 * �����̃I�u�W�F�N�g�i�P�O�O�O�`�j�𐶐������ꍇ�ɂ̓X���b�h�����啝�ɑ������A�����\�͂̒�����
	 * �ቺ�������\��������܂��B
	 * 
	 * @see #XMLSocket(SelectorThread)
	 */
	public XMLSocket() throws IOException {
		this(new XMLSocketThread(new ExceptionListener(){
				public void onError(Exception e) {
					onError(e);
				}
			}));
	}

	/**
	 * XMLSocket �ʐM�I�u�W�F�N�g�𐶐����܂��B java.nio �p�b�P�[�W�𗘗p���X���b�h������������
	 * �ʐM���������邽�߂Ɉ����ɃX���b�h�I�u�W�F�N�g������Ă��܂�<br>
	 * <br>
	 * �X���b�h�͂P�O�O�`�Q�O�O���x�ł���Ζ��Ȃ����삵�܂����A�P�O�O�O�`�@�̃X���b�h���͑����̂n�r�ő�
	 * ���ȏ����\�͂̒ቺ�������N�����܂��B���ׁ̈A���ɑ����̒ʐM�Z�b�V�������J���ꍇ�ɂ́A���ʂ�
	 * �X���b�h�𗘗p���ăX���b�h�����������Ă��������B<br>
	 * 
	 * @param thread ���p����X���b�h���w��
	 */
	public XMLSocket(XMLSocketThread thread) {
		super(thread, 
				new XMLSocketProtocolEncoder(), 
				new XMLSocketProtocolDecoder());
	}
	
	XMLSocket(SelectorThread thread) {
		super(thread, 
				new XMLSocketProtocolEncoder(), 
				new XMLSocketProtocolDecoder());
	}
	
	private boolean parsexml = true;
	private LinkedList xmllistener = new LinkedList();
	
	private Charset inputcs  = Charset.forName("UTF-8");
	private Charset outputcs = Charset.forName("UTF-8");
	private String ipaddress = "";
	
	/**
	 * XMLSocket �ʐM�ɗ��p���镶���R�[�h�̃G���R�[�f�B���O���w�肵�܂��B
	 * �w�肵�Ȃ��ꍇ�W���ł� UTF-8 ���ݒ肳��Ă��܂����A�ʏ�� Flash �Ɠ��{��ŒʐM����ɂ�
	 * ShiftJIS �ł���K�v������܂��B
	 * 
	 * @param charset ���o�͂̕����R�[�h���w��
	 */
	public void setEncoding(Charset charset){
		setEncoding(charset,charset);
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
		setEncoding(Charset.forName(input), Charset.forName(output));
	}
	
	/**
	 * ������ str �𑊎葤�ɑ���܂��B XMLSocket �ʐM�ł� str �͒ʏ� well-formed XML
	 * �ł��� �K�v������܂��B
	 * 
	 * @param str ���M���镶���� �iXML �ł���ׂ��ł��j
	 */
	public void send(String str) {
		try {
			send(str.getBytes(outputcs.displayName()));
			log.debug("SEND " + ipaddress + " " + str);
		} catch (UnsupportedEncodingException e) {}
	}

	/**
	 * XML �h�L�������g�𑊎葤�ɑ���܂��B
	 * 
	 * @param doc ���M���� XML �h�L�������g
	 */
	public void send(Document doc) throws TransformerException {
		Transformer t = XMLTools.transformerFactory.newTransformer();
		t.setOutputProperty(OutputKeys.ENCODING,outputcs.displayName());
		String str = XMLTools.write(doc,t);
		try {
			send(str.getBytes(outputcs.displayName()));
			log.debug("SEND " + ipaddress + " " + str);
		} catch (UnsupportedEncodingException e) {}
	}

	/**
	 * XML ��͂��s�����ǂ����̐ݒ�����܂��Btrue �ɂ����ꍇ�ɂ͏�� XML ��͂��s���܂����A
	 * false �ɂ���� XML�@��͂��s���Ȃ��Ȃ�AonXML(Document) ���Ăяo����邱�Ƃ��Ȃ��Ȃ�܂��B
	 * 
	 * @param bool
	 *            XML ��͂̍s��/�s��Ȃ�
	 */
	public void setXMLParsing(boolean bool) {
		parsexml = bool;
	}

	/**
	 * �ڑ����s���Ă��� Socket �N���X�̃C���X�^���X��Ԃ��܂��B
	 * 
	 * @return �ڑ����\�P�b�g
	 */
	public Socket getSocket() {
		return getSocketChannel().socket();
	}

	/**
	 * �ڑ��J�n���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��Bsuccess == false �̎���
	 * <b>�ʐM���m������Ă��܂���B</b>
	 * ���̏ꍇ�ɂ͒ʏ� onError ���Ă΂�܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onConnect(boolean) �C�x���g�� 
	 * �擾�ł��܂��B
	 * 
	 * @param success
	 *            �ڑ��̐���
	 */
	public void onConnect(boolean success) {
		ipaddress = getSocket().getInetAddress().getHostAddress();
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onConnect(success);
		}
		if(success) log.debug("CONNECT " + ipaddress);
	}

	/**
	 * �ؒf�������ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onClose() �C�x���g�� �擾�ł��܂�
	 */
	public void onClose() {
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onClose();
		}
		log.debug("CLOSE " + ipaddress);
	}

	/**
	 * �G���[���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onError(Exception) �C�x���g�� 
	 * �擾�ł��܂��B
	 * 
	 * @param e
	 *            �G���[
	 */
	public void onError(Exception e) {
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onError(e);
		}
		log.debug("ERROR " + ipaddress,e);
	}

	/**
	 * �ʐM�^�C���A�E�g���ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B�^�C���A�E�g�͂U�O�b���Ƃ� 
	 * ���s����܂��B <br>
	 * <br>
	 * <b>���ӁF</b><code>onTimeout()</code> �͒ʐM���s���Ă��Ȃ���ΗႦ�ڑ����p������
	 * ����ꍇ�ł����s����܂����A������ <code>onTimeout()</code> �����s���ꑱ����ꍇ�ɂ�
	 * �ڑ����ؒf����Ă���\��������܂��B�N���C�A���g��݌v����ꍇ�ɂ͕K������I�� PING �i�ڑ�
	 * ����̐����m�F�j�𑗂�A���� <code>onTimeout()</code> �����s�����^�C�~���O�ő��肩��
	 * �̑��M�������ԓr�₵�Ă���ꍇ�ɂ̓\�P�b�g���I�����鏈�����K�v�ł��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� <code>onTimeout()</code> �C�x���g�� 
	 * �擾�ł��܂�
	 * 
	 * @see java.net.Socket#setSoTimeout(int)
	 */
	public void onTimeout() {
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onTimeout();
		}
		log.debug("TIMEOUT " + ipaddress);
	}
	
	public void onPacket(byte[] b) {
		try {
			onData(new String(b,inputcs.displayName()));
		} catch (UnsupportedEncodingException e) {}
	}
	
	/**
	 * �f�[�^��M���ɌĂ΂�AXMLSocketListener �ɒʒm���AXML ��͂��s���� onXML(Document) 
	 * ���Ăяo���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onData(String) �C�x���g��
	 * �擾�ł��܂��B���̃��\�b�h���p�������ꍇ�Asuper.onData(String) ���Ă΂Ȃ��� onXML(Document)
	 * �C�x���g���Ă΂�Ȃ��Ȃ�܂��B
	 * 
	 * @see #onXML(Document)
	 */
	protected void onData(String data) {
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onData(data);
		}
		log.debug("DATA " + ipaddress + " " + data);
		try {
			onXML(XMLTools.read(data));
		} catch (SAXException e) {}
	}

	/**
	 * �f�[�^��M��̂���� XML ��͌�A�ɌĂ΂�AXMLSocketListener �ɒʒm���܂��B<BR>
	 * <BR>
	 * ���̃N���X���p�������N���X�����ꍇ�ɂ͂��̃��\�b�h���p�����邱�Ƃ� onXML(Document) �C�x���g��
	 * �擾�ł��܂��B���̃C�x���g���擾����ɂ� setXMLParseing(boolean) �� true (default)
	 * ���ݒ肳��Ă���K�v������܂��B
	 */
	protected void onXML(Document doc) {
		for (Iterator iter = xmllistener.iterator(); iter.hasNext();) {
			((XMLSocketListener) iter.next()).onXML(doc);
		}
	}

	/**
	 * �C�x���g�n���h����o�^���܂��B���̃��\�b�h�𗘗p���� XMLSocketListener �����������N���X��
	 * �o�^���ăC�x���g���擾�A�K�X�������s���Ă��������B
	 * 
	 * @param listener
	 *            �C�x���g�n���h��
	 */
	public void addXMLSocketListener(XMLSocketListener listener) {
		xmllistener.add(listener);
	}

	/**
	 * �o�^�����C�x���g�n���h�����폜���܂��B
	 * 
	 * @param listener
	 *            �C�x���g�n���h��
	 */
	public void removeXMLSocketListener(XMLSocketListener listener) {
		xmllistener.remove(listener);
	}
}
