#ifndef SRC_CLIENT_H_
#define SRC_CLIENT_H_
#include "WPILib.h"

class Client {

	public static double m_targetPosition;
	Client();

	void sendPacket();

	virtual ~Client();

};

#endif
