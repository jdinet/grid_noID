#include "block.bbh"
#include "clock.bbh"
#include "math.h"


threaddef #define UNKNOWN		255

threadvar byte position[2];
threadvar byte tab[2];
threadvar byte posSpawner[2];
threadvar byte delCouleur[4];
threadvar byte topCouleur[4];

threadvar byte lien;
threadvar byte nbreWaitedAnswers;
threadvar byte xplusBorder;
threadvar byte yplusBorder;
threadvar byte fpos;
threadvar byte forme;
threadvar byte rota;
threadvar byte countspawn;
threadvar byte bigShaq;
threadvar byte lineCounter;
threadvar byte compteurAleat;
threadvar byte freeLeft;
threadvar byte freeRight;
threadvar byte freeDown;

threadvar Timeout scrollTimeout;
threadvar Timeout eraseTimeout;


threadvar byte sample[7][4][9];
threadvar byte couleurForme[7][4];


threaddef  #define MYCHUNKS 12
threadextern Chunk* thisChunk;
threadvar  Chunk myChunks[MYCHUNKS];

 /***************/
 /** functions **/
 /***************/
 byte goMessageHandler(void);
 byte sendBackChunk(PRef p);
 byte backMessageHandler(void);
 byte sendCoordChunk(PRef p);
 byte coordMessageHandler(void);
 byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc);
 byte execOn(void);
 byte spawner(void);
 byte floodSpawner(PRef p);
 byte findSpawner(void);
 byte getSpawn(uint8_t donnee,uint8_t t);
 byte sendShape(PRef p);
 byte shapeMessageHandler(void);
 byte goingDown(void);
 byte miseAZero(PRef p);
 byte miseAZeroMessageHandler(void);
 byte areYouBlocked(PRef p);
 byte blockedAnswer(PRef p);
 byte blockedCheck (PRef pork);
 byte blockedRequest (void);
 byte sendStop(PRef p);
 byte everybodyStopNow(void);
 byte blockedFinal(void);
 byte alea(void);
 byte lineCompletedTest(void);
 byte lineCompletedTestHandler(void);
 byte lineErase(void);
 byte sendLineDown(void);
 byte delDownHandler (void);
 byte sendDelDown(void);
 byte lineDownHandler(void);
 byte motionControl(PRef pork);


/******************************/
 void myMain(void) {

   delayMS(200);
   compteurAleat = 0;
   lien=UNKNOWN;
   nbreWaitedAnswers=0;
   position[0] = 0;
   position[1] = 0;
   xplusBorder = WEST;
   yplusBorder = UP;
   fpos = UNKNOWN;
 	 forme = UNKNOWN;
 	 rota = UNKNOWN;

   bigShaq = UNKNOWN;
   lineCounter = 0;

   posSpawner[0] = UNKNOWN;
   posSpawner[1] = UNKNOWN;

   freeLeft = 0;
   freeRight = 0;
   freeDown = 0;

   // put into standby mode to update registers
   setAccelRegister(0x07, 0x18);

   // every measurement triggers an interrupt
   setAccelRegister(0x06, 0x10);

   // set filter rate
   setAccelRegister(0x08, 0x00);

   // enable accelerometer
   setAccelRegister(0x07, 0x19);

//CARRE
  sample[0][0][0]=1; sample[0][0][1]=1; sample[0][0][2]=0; sample[0][0][3]=1; sample[0][0][4]=1; sample[0][0][5]=0; sample[0][0][6]=0; sample[0][0][7]=0; sample[0][0][8]=0;
 	sample[0][1][0]=1; sample[0][1][1]=1; sample[0][1][2]=0; sample[0][1][3]=1; sample[0][1][4]=1; sample[0][1][5]=0; sample[0][1][6]=0; sample[0][1][7]=0; sample[0][1][8]=0;
 	sample[0][2][0]=1; sample[0][2][1]=1; sample[0][2][2]=0; sample[0][2][3]=1; sample[0][2][4]=1; sample[0][2][5]=0; sample[0][2][6]=0; sample[0][2][7]=0; sample[0][2][8]=0;
 	sample[0][3][0]=1; sample[0][3][1]=1; sample[0][3][2]=0; sample[0][3][3]=1; sample[0][3][4]=1; sample[0][3][5]=0; sample[0][3][6]=0; sample[0][3][7]=0; sample[0][3][8]=0;

//L
 	sample[1][0][0]=0; sample[1][0][1]=1; sample[1][0][2]=0; sample[1][0][3]=0; sample[1][0][4]=1; sample[1][0][5]=0; sample[1][0][6]=0; sample[1][0][7]=1; sample[1][0][8]=1;
 	sample[1][1][0]=0; sample[1][1][1]=0; sample[1][1][2]=0; sample[1][1][3]=1; sample[1][1][4]=1; sample[1][1][5]=1; sample[1][1][6]=1; sample[1][1][7]=0; sample[1][1][8]=0;
 	sample[1][2][0]=1; sample[1][2][1]=1; sample[1][2][2]=0; sample[1][2][3]=0; sample[1][2][4]=1; sample[1][2][5]=0; sample[1][2][6]=0; sample[1][2][7]=1; sample[1][2][8]=0;
 	sample[1][3][0]=0; sample[1][3][1]=0; sample[1][3][2]=1; sample[1][3][3]=1; sample[1][3][4]=1; sample[1][3][5]=1; sample[1][3][6]=0; sample[1][3][7]=0; sample[1][3][8]=0;

//S
 	sample[2][0][0]=1; sample[2][0][1]=0; sample[2][0][2]=0; sample[2][0][3]=1; sample[2][0][4]=1; sample[2][0][5]=0; sample[2][0][6]=0; sample[2][0][7]=1; sample[2][0][8]=0;
 	sample[2][1][0]=0; sample[2][1][1]=1; sample[2][1][2]=1; sample[2][1][3]=1; sample[2][1][4]=1; sample[2][1][5]=0; sample[2][1][6]=0; sample[2][1][7]=0; sample[2][1][8]=0;
 	sample[2][2][0]=0; sample[2][2][1]=1; sample[2][2][2]=0; sample[2][2][3]=0; sample[2][2][4]=1; sample[2][2][5]=1; sample[2][2][6]=0; sample[2][2][7]=0; sample[2][2][8]=1;
 	sample[2][3][0]=0; sample[2][3][1]=0; sample[2][3][2]=0; sample[2][3][3]=0; sample[2][3][4]=1; sample[2][3][5]=1; sample[2][3][6]=1; sample[2][3][7]=1; sample[2][3][8]=0;

//T
 	sample[3][0][0]=0; sample[3][0][1]=1; sample[3][0][2]=0; sample[3][0][3]=1; sample[3][0][4]=1; sample[3][0][5]=1; sample[3][0][6]=0; sample[3][0][7]=0; sample[3][0][8]=0;
 	sample[3][1][0]=0; sample[3][1][1]=1; sample[3][1][2]=0; sample[3][1][3]=0; sample[3][1][4]=1; sample[3][1][5]=1; sample[3][1][6]=0; sample[3][1][7]=1; sample[3][1][8]=0;
 	sample[3][2][0]=0; sample[3][2][1]=0; sample[3][2][2]=0; sample[3][2][3]=1; sample[3][2][4]=1; sample[3][2][5]=1; sample[3][2][6]=0; sample[3][2][7]=1; sample[3][2][8]=0;
 	sample[3][3][0]=0; sample[3][3][1]=1; sample[3][3][2]=0; sample[3][3][3]=1; sample[3][3][4]=1; sample[3][3][5]=0; sample[3][3][6]=0; sample[3][3][7]=1; sample[3][3][8]=0;

//I
 	sample[4][0][0]=0; sample[4][0][1]=1; sample[4][0][2]=0; sample[4][0][3]=0; sample[4][0][4]=1; sample[4][0][5]=0; sample[4][0][6]=0; sample[4][0][7]=1; sample[4][0][8]=0;
 	sample[4][1][0]=0; sample[4][1][1]=0; sample[4][1][2]=0; sample[4][1][3]=1; sample[4][1][4]=1; sample[4][1][5]=1; sample[4][1][6]=0; sample[4][1][7]=0; sample[4][1][8]=0;
 	sample[4][2][0]=0; sample[4][2][1]=1; sample[4][2][2]=0; sample[4][2][3]=0; sample[4][2][4]=1; sample[4][2][5]=0; sample[4][2][6]=0; sample[4][2][7]=1; sample[4][2][8]=0;
 	sample[4][3][0]=0; sample[4][3][1]=0; sample[4][3][2]=0; sample[4][3][3]=1; sample[4][3][4]=1; sample[4][3][5]=1; sample[4][3][6]=0; sample[4][3][7]=0; sample[4][3][8]=0;

//Z
  sample[5][0][0]=0; sample[5][0][1]=0; sample[5][0][2]=1; sample[5][0][3]=0; sample[5][0][4]=1; sample[5][0][5]=1; sample[5][0][6]=0; sample[5][0][7]=1; sample[5][0][8]=0;
 	sample[5][1][0]=1; sample[5][1][1]=1; sample[5][1][2]=0; sample[5][1][3]=0; sample[5][1][4]=1; sample[5][1][5]=1; sample[5][1][6]=0; sample[5][1][7]=0; sample[5][1][8]=0;
 	sample[5][2][0]=0; sample[5][2][1]=1; sample[5][2][2]=0; sample[5][2][3]=1; sample[5][2][4]=1; sample[5][2][5]=0; sample[5][2][6]=1; sample[5][2][7]=0; sample[5][2][8]=0;
 	sample[5][3][0]=1; sample[5][3][1]=1; sample[5][3][2]=0; sample[5][3][3]=0; sample[5][3][4]=1; sample[5][3][5]=1; sample[5][3][6]=0; sample[5][3][7]=0; sample[5][3][8]=0;

  //J
  sample[6][0][0]=0; sample[6][0][1]=1; sample[6][0][2]=0; sample[6][0][3]=0; sample[6][0][4]=1; sample[6][0][5]=0; sample[6][0][6]=1; sample[6][0][7]=1; sample[6][0][8]=0;
 	sample[6][1][0]=1; sample[6][1][1]=0; sample[6][1][2]=0; sample[6][1][3]=1; sample[6][1][4]=1; sample[6][1][5]=1; sample[6][1][6]=0; sample[6][1][7]=0; sample[6][1][8]=0;
 	sample[6][2][0]=0; sample[6][2][1]=1; sample[6][2][2]=1; sample[6][2][3]=0; sample[6][2][4]=1; sample[6][2][5]=0; sample[6][2][6]=0; sample[6][2][7]=1; sample[6][2][8]=0;
 	sample[6][3][0]=0; sample[6][3][1]=0; sample[6][3][2]=0; sample[6][3][3]=1; sample[6][3][4]=1; sample[6][3][5]=1; sample[6][3][6]=0; sample[6][3][7]=0; sample[6][3][8]=1;

  /*
  couleurForme[4][0] = 40; couleurForme[0][1] = 255; couleurForme[0][2] = 40; couleurForme[0][3] = 255;
  couleurForme[3][0] = 75; couleurForme[1][1] = 0; couleurForme[1][2] = 255; couleurForme[1][3] = 255;
  couleurForme[0][0] = 255; couleurForme[2][1] = 255; couleurForme[2][2] = 5; couleurForme[2][3] = 255;
  couleurForme[3][0] = 255; couleurForme[3][1] = 9; couleurForme[3][2] = 33; couleurForme[3][3] = 255;//Z
  couleurForme[1][0] = 231; couleurForme[4][1] = 61; couleurForme[4][2] = 1; couleurForme[4][3] = 255;
  */

  couleurForme[0][0] = 255; couleurForme[0][1] = 255; couleurForme[0][2] = 0; couleurForme[0][3] = 255;
  couleurForme[1][0] = 255; couleurForme[1][1] = 80; couleurForme[1][2] = 0; couleurForme[1][3] = 255;
  couleurForme[2][0] = 0; couleurForme[2][1] = 255; couleurForme[2][2] = 0; couleurForme[2][3] = 255;
  couleurForme[3][0] = 75; couleurForme[3][1] = 0; couleurForme[3][2] = 255; couleurForme[3][3] = 255;
  couleurForme[4][0] = 40; couleurForme[4][1] = 255; couleurForme[4][2] = 60; couleurForme[4][3] = 255;
  couleurForme[5][0] = 255; couleurForme[5][1] = 0; couleurForme[5][2] = 0; couleurForme[5][3] = 255;
  couleurForme[6][0] = 0; couleurForme[6][1] = 0; couleurForme[6][2] = 255; couleurForme[6][3] = 255;





  topCouleur[0] = 0; topCouleur[1] = 0; topCouleur[2] = 0; topCouleur[3] = 0;

  delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;



   if (thisNeighborhood.n[DOWN] == VACANT && thisNeighborhood.n[EAST] == VACANT) {

          setColor(RED);
          position[0]=127;
          position[1]=127;

          for (uint8_t p=0; p<6; p++) {
            if (thisNeighborhood.n[p] != VACANT) {
               sendCoordChunk(p);
                 nbreWaitedAnswers++;
             }
         }
     }


 while(1) {
   delayMS(50);
         //compteurAleat++;


   }
}


/******************/
/**** systeme ****/
/******************/

void userRegistration(void) {
    registerHandler(SYSTEM_MAIN, (GenericHandler)&myMain);
}

void freeMyChunk(void) {
  freeChunk(thisChunk);
}


// find a useable chunk
Chunk* getFreeUserChunk(void) {
    Chunk* c;
    int i;

    for(i=0; i<MYCHUNKS; i++) {
        c = &(myChunks[i]);
        if(!chunkInUse(c)) {
            return c;
        }
    }
    return NULL;
}


/***********************/
/** FONCTION EMISSION**/
/**********************/


//***Distribution des coordonnées au voisin***//
byte sendCoordChunk(PRef p) {
      Chunk *c = getFreeUserChunk();

      if (c != NULL) {
          c->data[0]=position[0];
          c->data[1]=position[1];
          if (p==xplusBorder) {
              c->data[0]++;
          } else if (p==5-xplusBorder) {
              c->data[0]--;
          } else if (p==yplusBorder) {
              c->data[1]++;
          } else if (p==5-yplusBorder) {
              c->data[1]--;
          }
          if (sendMessageToPort(c, p, c->data, 2, coordMessageHandler,
 (GenericHandler)&freeMyChunk) == 0) {
              freeChunk(c);
              return 0;
          }
      }
      return 1;
 }

//***Acknowledge***//
 byte sendBackChunk(PRef p) {
     Chunk *c=getFreeUserChunk();
     c->data[0]=position[0];
     c->data[1]=position[1];

     if (c != NULL) {
         if (sendMessageToPort(c, p, c->data, 2, backMessageHandler, &freeMyChunk) == 0) {
             freeChunk(c);
             return 0;
         }
     }
     return 1;
 }



/**************************/
/** FONCTIONS RECEPTION **/
/*************************/

 byte coordMessageHandler(void) {
      if (thisChunk == NULL) return 0;
      byte sender = faceNum(thisChunk);

      #ifdef BBSIM
      delayMS(100);
      #endif


      //***Je reçois des coordonnées identiques aux miennes***//
      if (position[0] == thisChunk->data[0] && position[1] == thisChunk->data[1]){
        sendBackChunk(sender);
        return 1;
      }

      //***Je reçois pour la première fois mes coordonnées***//
      else if (position[0] == 0 && position[1] == 0) {
        position[0] = thisChunk->data[0];
        position[1] = thisChunk->data[1];
        lien = sender;
        setColor(GREEN);
         for (uint8_t p=0; p<6; p++) {
          if (p!=sender && thisNeighborhood.n[p] != VACANT) {
            sendCoordChunk(p);
            nbreWaitedAnswers++;
            }
        }
    }

      //***Je reçois des coordonnées meilleurs que mes coordonnées actuelles***//
      else if (position[1] < thisChunk->data[1] || (position[1] == thisChunk->data[1] && position[0] < thisChunk->data[0])) {


          position[0] = thisChunk->data[0];
          position[1] = thisChunk->data[1];
          lien = sender;
          setColor(BLUE);
          nbreWaitedAnswers=0;
          for (uint8_t p=0; p<6; p++) {
           if (p!=sender && thisNeighborhood.n[p] != VACANT) {

             sendCoordChunk(p);
             nbreWaitedAnswers++;
              }
           }
        }


        //***Je reçois des coordonnées moins bonne que mes coordonnées actuelles***//
        else if (position[0] < thisChunk->data[0] || position[1] < thisChunk->data[1]) {
          sendCoordChunk(sender);
          nbreWaitedAnswers++;
        }



      if (nbreWaitedAnswers==0 && lien != UNKNOWN){
          sendBackChunk(lien);
        }

      return 1;
}



 byte backMessageHandler(void) {
   if (thisChunk==NULL) return 0;
   uint8_t sender=faceNum(thisChunk);

   #ifdef BBSIM
   delayMS(300);
   #endif

   if ( (sender==xplusBorder && thisChunk->data[0] == (position[0]+1) && thisChunk->data[1] == position[1]) ||
        (sender==5-xplusBorder && thisChunk->data[0] == (position[0]-1) && thisChunk->data[1] == position[1]) ||
        (sender==yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]+1)) ||
        (sender==5-yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]-1)) ) {

   nbreWaitedAnswers--;

   if (nbreWaitedAnswers==0 && lien == UNKNOWN){
     setLED(0,0,0,0);
     delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;

     #ifdef BBSIM
     setColor(WHITE);
     #endif
   }



   if (nbreWaitedAnswers==0 && lien != UNKNOWN) {

     setLED(0,0,0,0);
     delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;

     #ifdef BBSIM
     setColor(WHITE);
     #endif
     sendBackChunk(lien);

     if (thisNeighborhood.n[WEST] == VACANT && thisNeighborhood.n[DOWN] == VACANT ){
       delayMS(200);

     uint8_t width = position[0]-126; // afin de se compter dans le caclul
     sendExecOn(EAST,127,127,width,147);
   }


     else if (thisNeighborhood.n[EAST] == VACANT && thisNeighborhood.n[UP] == VACANT ){
       delayMS(200);

     uint8_t height = position[1]-126;
     sendExecOn(DOWN,127,127,height,148);
      }
    }
  }
   return 1;
 }


//*** +++ ***//


byte sendExecOn(PRef p, byte px, byte py, byte donnee, byte fonc) {
	Chunk *c = getFreeUserChunk();

  #ifdef BBSIM
  delayMS(50);
  #endif


    if (c!=NULL) {

		c->data[0] = px;
		c->data[1] = py;
		c->data[2] = donnee;
		c->data[3] = fonc;

		if (sendMessageToPort(c, p, c->data, 4, execOn, (GenericHandler)&freeMyChunk) == 0) {
			freeChunk(c);
			return 0;
		}
	}
	return 1;
}



	byte execOn(void) {
			if (thisChunk==NULL) return 0;
			uint8_t receiver[2];
			receiver[0] = thisChunk->data[0];
			receiver[1] = thisChunk->data[1];
			uint8_t donnee = thisChunk->data[2];
			byte fonc = thisChunk->data[3];


      #ifdef BBSIM
      delayMS(50);
      #endif

      if (donnee > 100){

        if (fonc == 178){
          if (donnee <= 102)
            eraseTimeout.calltime = getTime() + 300;
          else if (donnee <= 105)
            eraseTimeout.calltime = getTime() + 450;
          else if (donnee <= 108)
            eraseTimeout.calltime = getTime() + 600;
          eraseTimeout.callback = (GenericHandler)(&lineErase);
          registerTimeout(&eraseTimeout);
        }

      }

			if (position[0] != receiver[0]){

				if (position[0] < receiver[0] && thisNeighborhood.n[WEST] != VACANT)
					sendExecOn(WEST, receiver[0], receiver[1], donnee, fonc);

				else if (position[0] > receiver[0] && thisNeighborhood.n[EAST] != VACANT)
					sendExecOn(EAST, receiver[0], receiver[1], donnee, fonc);
			}


			else if (position[1] != receiver[1]){

				if (position[1] < receiver[1] && thisNeighborhood.n[UP] != VACANT)
					sendExecOn(UP, receiver[0], receiver[1], donnee, fonc);

				else if (position[1] > receiver[1] && thisNeighborhood.n[DOWN] != VACANT)
					sendExecOn(DOWN, receiver[0], receiver[1], donnee, fonc);
			}

      else if (position[0] == receiver[0] && position[1] == receiver[1]){

        if (fonc == 147)
          getSpawn(donnee,1);

        if (fonc == 148)
          getSpawn(donnee,2);

      if (fonc == 166){
        scrollTimeout.callback = (GenericHandler)(&spawner);
        scrollTimeout.calltime = getTime() + 700;
        registerTimeout(&scrollTimeout);
      }

      if (fonc == 176 && lineCounter > 0 && donnee == 0){
        lineCounter--;

      }

      if (fonc == 177 && position[0] == 127){
          lineCounter++;

          if (lineCounter == (2*(posSpawner[0] - 127) + 1)){
              sendExecOn(WEST, (2*(posSpawner[0] - 127) + 127), position[1], (100 + donnee), 178);
              if (donnee <= 2)
                eraseTimeout.calltime = getTime() + 300;
              else if (donnee <= 5)
                eraseTimeout.calltime = getTime() + 450;
              else if (donnee <= 8)
                eraseTimeout.calltime = getTime() + 600;
              eraseTimeout.callback = (GenericHandler)(&lineErase);
              registerTimeout(&eraseTimeout);
          }
      }

      if (fonc == 180 && fpos == 4){
        if (donnee == 0)
          freeDown = 1;
        else if (donnee == 2)
          freeLeft = 1;
        else if (donnee == 3)
          freeRight = 1;
      }

}

			return 1;
}

byte getSpawn(uint8_t donnee, uint8_t t){


  if (t == 1){
    tab[0] = (donnee/2);
    countspawn++;
  }
  else if (t == 2){
    tab[1] = donnee;
    countspawn++;
  }

  if (countspawn == 2){

    #ifdef BBSIM
    delayMS(50);
    #endif
  posSpawner[0] = 127+tab[0];
  posSpawner[1] = 126+tab[1];
  for (uint8_t p=0; p<6; p++) {
      if (thisNeighborhood.n[p] != VACANT)
        floodSpawner(p);
      }
  }

  return 1;
}

byte floodSpawner(PRef p){
  Chunk *c = getFreeUserChunk();

  c->data[0]=posSpawner[0];
  c->data[1]=posSpawner[1];

  if (c != NULL) {

      if (sendMessageToPort(c, p, c->data, 2, findSpawner,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
  }

  return 1;
}


byte findSpawner(void){
  if (thisChunk == NULL) return 0;
  byte sender = faceNum(thisChunk);

  if (posSpawner[0] == UNKNOWN){
    posSpawner[0] = thisChunk->data[0];
    posSpawner[1] = thisChunk->data[1];

    if (position[0] == posSpawner[0] && position[1] == posSpawner[1])
      spawner();
    else {
      for (uint8_t p=0; p<6; p++) {
          if (p != sender && thisNeighborhood.n[p] != VACANT)
            floodSpawner(p);
      }
    }
  }

  return 1;
}

byte spawner(void){

  if (bigShaq != 1) {

    AccelData acc = getAccelData();

    if (acc.z < 12 && forme != 0){

      rota = (rota + 1 ) % 4;


      if (position[0]==127) {

          fpos=3;

      }
      else if(position[0]==(2*(posSpawner[0] - 127)+ 127)) {

          fpos=5;

      }

      else {

        scrollTimeout.callback = (GenericHandler)(&spawner);
        scrollTimeout.calltime = getTime() + 1000;
        registerTimeout(&scrollTimeout);
      }
    }


/*
  else if (acc.x >= 8 && (((posSpawner[0] + 50) - position[0] < 51) || (((forme == 1 && rota == 0) ||
  (forme == 2 && rota == 2) || (forme == 3 && rota == 1) || (forme == 4 && rota == 0) ||
  (forme == 4 && rota == 2)) && ((posSpawner[0] + 50) - position[0] < 52)))) { // + 50 pour eviter soucis BYTE ( 0 -1 = 255)
      fpos = 5;
      //if (thisNeighborhood.n[WEST] != VACANT)
        miseAZero(WEST);
  }
*/

    else if (acc.x >= 7 && freeLeft != 1) {
        fpos = 5;
        if (thisNeighborhood.n[WEST] != VACANT)
          miseAZero(WEST);
    }


    else if (acc.x <= -7 && freeRight != 1) {
        fpos = 3;
        if (thisNeighborhood.n[EAST] != VACANT)
          miseAZero(EAST);
    }


  else if (freeDown != 1) {
    fpos = 1;
    if (thisNeighborhood.n[UP] != VACANT)
      miseAZero(UP);
  }

  else {
    for (uint8_t p=0; p<6; p++) {
      if (thisNeighborhood.n[p] != VACANT){

        sendStop(p);
      }
    }
  }


  if (position[0] == posSpawner[0] && position[1] == posSpawner[1] && forme == UNKNOWN){
    forme = alea() % 7;
    rota = alea() % 4;
  }

  if (sample[forme][rota][fpos] == 1){
    setLED(couleurForme[forme][0], couleurForme[forme][1], couleurForme[forme][2], couleurForme[forme][3]);
    delCouleur[0] = couleurForme[forme][0]; delCouleur[1] = couleurForme[forme][1]; delCouleur[2] = couleurForme[forme][2]; delCouleur[3] = couleurForme[forme][3];
  }
  else {
    setLED(0, 0, 0, 0);
    delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;

    #ifdef BBSIM
    setColor(WHITE);
    #endif
  }

      for (uint8_t p=0; p<6; p++) {
          if (thisNeighborhood.n[p] != VACANT)
              sendShape(p);
      }

      freeLeft = 0;
      freeRight = 0;
      freeDown = 0;
    }

  return 1;
}

byte sendShape(PRef p) {
      Chunk *c = getFreeUserChunk();

      if (c != NULL) {
          c->data[0]=forme;
          c->data[1]=rota;
          c->data[2]=fpos;
          if (p==xplusBorder && (fpos != 2 && fpos != 5 && fpos != 8))
              c->data[2]++;
          else if (p==5-xplusBorder && (fpos != 0 && fpos != 3 && fpos != 6))
              c->data[2]--;
          else if (p==yplusBorder)
              c->data[2]= c->data[2] - 3;
          else if (p==5-yplusBorder)
              c->data[2]= c->data[2] + 3;

          if ((c->data[2] <= 8 && c->data[2] >= 0) && (c->data[2] != fpos)){

          if (sendMessageToPort(c, p, c->data, 3, shapeMessageHandler,
 (GenericHandler)&freeMyChunk) == 0) {
              freeChunk(c);
              return 0;
          }
      }
    }
      return 1;
 }


 byte shapeMessageHandler(void) {
      if (thisChunk == NULL) return 0;
      byte sender = faceNum(thisChunk);


      if (fpos != thisChunk->data[2] || rota != thisChunk->data[1]) {
        if (bigShaq != 1) {
        forme = thisChunk->data[0];
        rota = thisChunk->data[1];
        fpos = thisChunk->data[2];


        if (sample[forme][rota][fpos] == 1){
          setLED(couleurForme[forme][0], couleurForme[forme][1], couleurForme[forme][2], couleurForme[forme][3]);
          delCouleur[0] = couleurForme[forme][0]; delCouleur[1] = couleurForme[forme][1]; delCouleur[2] = couleurForme[forme][2]; delCouleur[3] = couleurForme[forme][3];
        }
				else {
          //setColor(BLUE);
          setLED(0, 0, 0, 0);
          delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;

          #ifdef BBSIM
          setColor(WHITE);
          #endif

          #ifdef LOG_DEBUG
          char s[25];
          snprintf(s, 25*sizeof(char), "X:%d,Y:%d, FOURTH",position[0] ,position[1]);
          s[149] = '\0';
          printDebug(s);
          #endif
        }



        for (uint8_t p=0; p<6; p++) {
          if (p!=sender && thisNeighborhood.n[p] != VACANT){

            sendShape(p);
          }
        }


        if (fpos == 4){
          scrollTimeout.callback = (GenericHandler)(&spawner);
          scrollTimeout.calltime = getTime() + 1000;
          registerTimeout(&scrollTimeout);
        }

          blockedCheck(DOWN);


          blockedCheck(EAST);


          blockedCheck(WEST);



        }
      }

      return 1;
}


byte miseAZero(PRef p){
  Chunk *c = getFreeUserChunk();

  c->data[0] = 1;

  if (c != NULL) {

      if (sendMessageToPort(c, p, c->data, 1, miseAZeroMessageHandler,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }

  return 1;
}

byte miseAZeroMessageHandler(void){
  if (thisChunk == NULL) return 0;

  if (fpos == 1){
    if (thisNeighborhood.n[EAST] != VACANT)
      miseAZero(EAST);
    if (thisNeighborhood.n[WEST] != VACANT)
      miseAZero(WEST);
  }

  else if (fpos == 3 || fpos == 5){
    if (thisNeighborhood.n[UP] != VACANT)
      miseAZero(UP);
    if (thisNeighborhood.n[DOWN] != VACANT)
      miseAZero(DOWN);
  }

  if (bigShaq != 1) {
    setLED(0,0,0,0);
    delCouleur[0] = 0; delCouleur[1] = 0; delCouleur[2] = 0; delCouleur[3] = 0;

    #ifdef LOG_DEBUG
    char s[25];
    snprintf(s, 25*sizeof(char), "X:%d,Y:%d, FIFTH",position[0] ,position[1]);
    s[149] = '\0';
    printDebug(s);
    #endif

    #ifdef BBSIM
    setColor(WHITE);
    #endif
    fpos = UNKNOWN;
    forme = UNKNOWN;
    rota = UNKNOWN;
  }

  return 1;
}

byte areYouBlocked(PRef p){
  Chunk *c = getFreeUserChunk();

  c->data[0] = 1;

  if (c != NULL) {

      if (sendMessageToPort(c, p, c->data, 1, blockedRequest,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }

return 1;
}


byte blockedAnswer(PRef p){
  Chunk *c = getFreeUserChunk();



  c->data[0] = bigShaq;

  if (c != NULL) {

      if (sendMessageToPort(c, p, c->data, 1, blockedFinal,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }

  return 1;
}


byte blockedCheck (PRef pork){

if (sample[forme][rota][fpos] == 1){

    if (thisNeighborhood.n[pork] != VACANT)
      areYouBlocked(pork);


    else if (thisNeighborhood.n[pork] == VACANT)
      motionControl(pork);

    }

  return 1;
}

byte motionControl(PRef pork){

  if (pork == 0 && thisNeighborhood.n[pork] == VACANT){
    for (uint8_t p=0; p<6; p++) {
      if (thisNeighborhood.n[p] != VACANT){

        sendStop(p);
      }
    }
  }

  if (pork == 2 || pork == 3 || pork == 0){
    if (fpos <= 8 && fpos >= 0){
      if (fpos == 4){
        if (pork == 0)
          freeDown = 1;
        else if (pork == EAST)
          freeLeft = 1;
        else if (pork == WEST)
          freeRight = 1;
        return 1;
      }

      int lapostionX = 0;
      int lapostionY = 0;

      if (fpos >= 0 && fpos <= 2){
        lapostionY = -1;
      }
      else if (fpos >= 6 && fpos <= 8){
        lapostionY = 1;
      }

      if (fpos == 0 || fpos == 3 || fpos == 6){
        lapostionX = 1;
      }
      else if (fpos == 2 || fpos == 5 || fpos == 8){
        lapostionX = -1;
      }
      sendExecOn((5-pork), (position[0]+lapostionX), (position[1]+lapostionY), pork, 180);

    }
  }

  return 1;
}

byte blockedRequest (void){
  if (thisChunk == NULL) return 0;
  byte sender = faceNum(thisChunk);

  blockedAnswer(sender);

  return 1;
}

byte blockedFinal (void){
  if (thisChunk == NULL) return 0;
    byte voisinState = thisChunk->data[0];
    byte sender = faceNum(thisChunk);

    if (voisinState == 1)
      motionControl(sender);


  return 1;
}



byte sendStop(PRef p){
  Chunk *c = getFreeUserChunk();

  c->data[0] = 1;

  if (c != NULL) {

      if (sendMessageToPort(c, p, c->data, 1, everybodyStopNow,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }
  return 1;
}

byte  everybodyStopNow(void){
  if (thisChunk == NULL) return 0;
  byte sender = faceNum(thisChunk);


  if (fpos == 1 && thisNeighborhood.n[DOWN] != VACANT){
    sendStop(DOWN);
  }


  if (fpos <= 8 && fpos >= 0 && bigShaq != 1){

    if (sample[forme][rota][fpos] == 1){
      bigShaq = 1;
      if (thisNeighborhood.n[DOWN] != VACANT)
        sendDelDown();
      }

    if (fpos != 4){
      for (uint8_t p=0; p<6; p++) {
        if (p != sender && thisNeighborhood.n[p] != VACANT){
          sendStop(p);
        }
      }
    }

    if (fpos <= 8 && fpos >= 0 && bigShaq == 1){
      if (position[0] == 127){
        lineCounter++;

        if (lineCounter == (2*(posSpawner[0] - 127) + 1)){
            sendExecOn(WEST, (2*(posSpawner[0] - 127) + 127), position[1], (100 + fpos), 178);
            if (fpos <= 2)
              eraseTimeout.calltime = getTime() + 300;
            else if (fpos <= 5)
              eraseTimeout.calltime = getTime() + 450;
            else if (fpos <= 8)
              eraseTimeout.calltime = getTime() + 600;
            eraseTimeout.callback = (GenericHandler)(&lineErase);
            registerTimeout(&eraseTimeout);
          }
      }
      else {
        sendExecOn(EAST, 127, position[1], fpos, 177);

      }

      if (fpos == 4 && (posSpawner[1] - position[1]) >= 3){
        sendExecOn(UP, posSpawner[0], posSpawner[1], 0, 166);
      }

    }

    //if (thisNeighborhood.n[DOWN] != VACANT)


  fpos = UNKNOWN;
  forme = UNKNOWN;
  rota = UNKNOWN;

}

  return 1;
}

byte alea(void){

  AccelData acc = getAccelData();

  long nombre = exp(acc.x)+exp(acc.z)+getTime()+compteurAleat;

  return nombre;

}

byte lineCompletedTest(void){
  Chunk *c = getFreeUserChunk();

  c->data[0] = 1;

  if (c != NULL) {

      if (sendMessageToPort(c, UP, c->data, 1, sendDelDown,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }
  return 1;
}


byte lineErase(void){
  delayMS(100);

  if (position[1] <= (posSpawner[1] - 1) && fpos == UNKNOWN){

    setLED(topCouleur[0], topCouleur[1], topCouleur[2], topCouleur[3]);
    delCouleur[0] = topCouleur[0]; delCouleur[1] = topCouleur[1]; delCouleur[2] = topCouleur[2]; delCouleur[3] = topCouleur[3];

    if (bigShaq == UNKNOWN && topCouleur[3] == 255){
      bigShaq = 1;

      if (position[0] == 127){
        lineCounter++;
      }
      else {
        sendExecOn(EAST, 127, position[1], fpos, 177);


        /*
        #ifdef LOG_DEBUG
        char s[25];
        snprintf(s, 25*sizeof(char), "compteur++, %d, %d", position[0], position[1]);
        s[149] = '\0';
        printDebug(s);
        #endif
        */


      }
    }
    else if (bigShaq == 1 && topCouleur[0] == 0){
      bigShaq = UNKNOWN;
      if (position[0] == 127 && lineCounter > 0){
        lineCounter--;
      }
      else {
        sendExecOn(EAST, 127, position[1], 0, 176);
      }
    }

    sendLineDown();

    if (thisNeighborhood.n[DOWN] != VACANT)
      sendDelDown();

  }


  return 1;
}

byte sendLineDown(void){
  Chunk *c = getFreeUserChunk();

  c->data[0] = 1;

  if (c != NULL) {

      if (sendMessageToPort(c, UP, c->data, 1, lineDownHandler,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }
  return 1;
}

byte lineDownHandler(void){
  if (thisChunk == NULL) return 0;

  lineErase();

  return 1;
}

byte sendDelDown(void){
  Chunk *c = getFreeUserChunk();

  c->data[0] = delCouleur[0]; c->data[1] = delCouleur[1]; c->data[2] = delCouleur[2]; c->data[3] = delCouleur[3];

  if (c != NULL) {

      if (sendMessageToPort(c, DOWN, c->data, 4, delDownHandler,(GenericHandler)&freeMyChunk) == 0) {
          freeChunk(c);
          return 0;
        }
      }
  return 1;
}

byte delDownHandler (void){
  if (thisChunk == NULL) return 0;

  topCouleur[0] = thisChunk->data[0];
  topCouleur[1] = thisChunk->data[1];
  topCouleur[2] = thisChunk->data[2];
  topCouleur[3] = thisChunk->data[3];


  return 1;
}
