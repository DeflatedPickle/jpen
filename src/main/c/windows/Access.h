/* [{
Copyright 2007, 2008 Nicolas Carranza <nicarran at gmail.com>

This file is part of jpen.

jpen is free software: you can redistribute it and/or modify
it under the terms of the GNU Lesser General Public License as published by
the Free Software Foundation, either version 3 of the License,
or (at your option) any later version.

jpen is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU Lesser General Public License for more details.

You should have received a copy of the GNU Lesser General Public License
along with jpen.  If not, see <http://www.gnu.org/licenses/>.
}] */
#ifndef Access_h
#define Access_h
#include "macros.h"
#include <jni.h>
#include <windows.h>
#include "INCLUDE/WINTAB.H"
//#ifdef USE_X_LIB
//#include "INCLUDE/WINTABX.h"
//#endif
//#define PACKETTILT	PKEXT_ABSOLUTE
#define PACKETDATA	(PK_STATUS | PK_TIME | PK_X | PK_Y | PK_NORMAL_PRESSURE | PK_CURSOR | PK_BUTTONS | PK_ORIENTATION | PK_TANGENT_PRESSURE /*| PK_ROTATION causes packet corruption :-(... symptom on JPen Demo: wild creation of inexistent PenDevices.  */)
#define PACKETMODE	0
// #define PACKETTILT PKEXT_ABSOLUTE // TILT extension is not widely implemented (wacom), so I disable it and use pkOrientation to get tilt data 
#include "INCLUDE/PKTDEF.H"

//vvv Taken from csrmaskex wacom example.
// cellosoft.jtablet is also a good example. Thanks marcello (cellosoft)!!
#define CSR_TYPE_GENERAL_MASK		( ( UINT ) 0xC000 )
#define CSR_TYPE_GENERAL_PENTIP		( ( UINT ) 0x4000 )
#define CSR_TYPE_GENERAL_PUCK		( ( UINT ) 0x8000 )
#define CSR_TYPE_GENERAL_PENERASER	( ( UINT ) 0xC000 )

// The CSR_TYPE WTInfo data item is new to Wintab 1.2 and is not defined
// in the Wintab 1.26 SDK, so we have to define it.
#ifndef CSR_TYPE
#	define CSR_TYPE 20
#endif
//^^^

#define QUEUE_SIZE 32

/* This enum matches the PLevel.Type enumeration: */
enum{
	E_Valuators_x,
	E_Valuators_y,
	E_Valuators_press,
	E_Valuators_orAzimuth,
	E_Valuators_orAltitude,
	E_Valuators_tanPress,
	E_Valuators_twist,
	E_Valuators_size,
};

enum{
	E_csrTypes_undef,
	E_csrTypes_penTip,
	E_csrTypes_puck,
	E_csrTypes_penEraser,
};
struct Access {
	int cellIndex;
	LOGCONTEXT lc;
	HCTX ctx;
	UINT device;
	int enabled;
	int valuatorValues[E_Valuators_size];
	PACKET queue[QUEUE_SIZE]; // buffer to get many packets at once on WTPacketsGet
	int queueSize;// index of the next to last packet usable (consumable) on queue
	int queueConsumableIndex; // index of queue element not yet consumed from the queue
	UINT cursor;
	DWORD buttons;
	UINT status;
	LONG time;
};
m_declareRow(Access);
extern int Access_nextPacket(SAccess *pAccess);
extern int Access_getEnabled(SAccess *pAccess);
extern void Access_setEnabled(SAccess *pAccess, int enabled);
extern void Access_enable(SAccess *pAccess, int enable);
extern void Access_getValuatorRange(SAccess *pAccess, int valuator, jint *pRange);
extern int Access_getCsrType(int cursor);
extern void Access_setSystemCursorEnabled(SAccess *pAccess, int enabled);
extern int Access_getSystemCursorEnabled(SAccess *pAccess);
#endif
