package com.jinzhu.motorcar;

import java.util.concurrent.Semaphore;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Camera extends Thread {

	private static Camera camera = null;

	private ICameraCallback callback = null;
	// �����ź������ڿ����߳�����״̬
	private Semaphore sem = null;

	public static Camera getinstance() {
		// ����ģʽ
		if (camera == null) {
			camera = new Camera();
		}
		return camera;
	}

	private Camera() {
		sem = new Semaphore(0);
		this.start();
	}

	public void run() {

		while (true) {
			// ʧ��������ôβ����������˳��߳�
			try {
				sem.acquire();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				continue;
			}

			try {
				// ��ȡͼƬ����ûص�����
				byte[] buffer = this.getPhoto();
				if (this.callback != null) {
					this.callback.cameraCallback(buffer);
				}

			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (this.callback != null) {
					this.callback.cameraCallback(null);
				}
				continue;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				if (this.callback != null) {
					this.callback.cameraCallback(null);
				}
				continue;
			}
		}

	}

	/***
	 * ��ȡͼƬ
	 * 
	 * @return
	 * @throws UnknownHostException
	 * @throws IOException
	 */
	private byte[] getPhoto() throws UnknownHostException, IOException {
		Socket sock = new Socket(Config.HostIP, Config.CameraPort);
		byte[] buffer = new byte[4];
		sock.getInputStream().read(buffer, 0, 4);
		// DATA_LEN = Byte 0 + (Byte 1<<8)+(Byte 2<<16)+(Byte 3<<24)
		int len = buffer[0] + (buffer[1] << 8) + (buffer[2] << 16) + (buffer[3] << 24);
		buffer = new byte[len];
		int recvlen = 0;
		while (recvlen < len) {
			int packetlen = len - recvlen;
			if (packetlen > 4 * 1024) {
				packetlen = 4 * 1024;
			}
			packetlen = sock.getInputStream().read(buffer, recvlen, packetlen);
			recvlen += packetlen;
		}
		sock.close();
		return buffer;
	}

	public void setCameraCallback(ICameraCallback callback) {
		this.callback = callback;
		return;
	}

	public void takePhoto() {
		if (sem.availablePermits() < 1) {
			// ȷ��API����ͬʱ���ö��
			sem.release();
		}
	}

}
