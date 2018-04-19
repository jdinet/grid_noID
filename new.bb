#include "block.bbh"
#include "clock.bbh"

threaddef #define UNKNOWN		255

threadvar byte position[2];
threadvar byte tab[2];
threadvar byte posSpawner[2];

threadvar  uint8_t lien;
threadvar  uint8_t nbreWaitedAnswers;
threadvar byte xplusBorder;
threadvar byte yplusBorder;
threadvar byte fpos;
threadvar byte forme;
threadvar byte rota;
threadvar byte countspawn;
threadvar byte bigShaq;

threadvar Timeout scrollTimeout;
threadvar Timeout spawnTimeout;


threadvar AccelData acc;

threadvar byte sample[5][4][9];


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
 byte blockedCheck (PRef p);
 byte blockedRequest (void);
 byte sendStop(PRef p);
 byte everybodyStopNow(void);
 byte blockedFinal (void);

/******************************/
 void myMain(void) {

   delayMS(300);
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

   posSpawner[0] = UNKNOWN;
   posSpawner[1] = UNKNOWN;


  sample[0][0][0]=1; sample[0][0][1]=1; sample[0][0][2]=1; sample[0][0][3]=1; sample[0][0][4]=1; sample[0][0][5]=1; sample[0][0][6]=1; sample[0][0][7]=1; sample[0][0][8]=1;
 	sample[0][1][0]=1; sample[0][1][1]=1; sample[0][1][2]=1; sample[0][1][3]=1; sample[0][1][4]=1; sample[0][1][5]=1; sample[0][1][6]=1; sample[0][1][7]=1; sample[0][1][8]=1;
 	sample[0][2][0]=1; sample[0][2][1]=1; sample[0][2][2]=1; sample[0][2][3]=1; sample[0][2][4]=1; sample[0][2][5]=1; sample[0][2][6]=1; sample[0][2][7]=1; sample[0][2][8]=1;
 	sample[0][3][0]=1; sample[0][3][1]=1; sample[0][3][2]=1; sample[0][3][3]=1; sample[0][3][4]=1; sample[0][3][5]=1; sample[0][3][6]=1; sample[0][3][7]=1; sample[0][3][8]=1;

 	sample[1][0][0]=0; sample[1][0][1]=1; sample[1][0][2]=0; sample[1][0][3]=0; sample[1][0][4]=1; sample[1][0][5]=0; sample[1][0][6]=0; sample[1][0][7]=1; sample[1][0][8]=1;
 	sample[1][1][0]=0; sample[1][1][1]=0; sample[1][1][2]=0; sample[1][1][3]=1; sample[1][1][4]=1; sample[1][1][5]=1; sample[1][1][6]=1; sample[1][1][7]=0; sample[1][1][8]=0;
 	sample[1][2][0]=1; sample[1][2][1]=1; sample[1][2][2]=0; sample[1][2][3]=0; sample[1][2][4]=1; sample[1][2][5]=0; sample[1][2][6]=0; sample[1][2][7]=1; sample[1][2][8]=0;
 	sample[1][3][0]=0; sample[1][3][1]=0; sample[1][3][2]=1; sample[1][3][3]=1; sample[1][3][4]=1; sample[1][3][5]=1; sample[1][3][6]=0; sample[1][3][7]=0; sample[1][3][8]=0;

 	sample[2][0][0]=1; sample[2][0][1]=0; sample[2][0][2]=0; sample[2][0][3]=1; sample[2][0][4]=1; sample[2][0][5]=0; sample[2][0][6]=0; sample[2][0][7]=1; sample[2][0][8]=0;
 	sample[2][1][0]=0; sample[2][1][1]=1; sample[2][1][2]=1; sample[2][1][3]=1; sample[2][1][4]=1; sample[2][1][5]=0; sample[2][1][6]=0; sample[2][1][7]=0; sample[2][1][8]=0;
 	sample[2][2][0]=0; sample[2][2][1]=1; sample[2][2][2]=0; sample[2][2][3]=0; sample[2][2][4]=1; sample[2][2][5]=1; sample[2][2][6]=0; sample[2][2][7]=0; sample[2][2][8]=1;
 	sample[2][3][0]=0; sample[2][3][1]=0; sample[2][3][2]=0; sample[2][3][3]=0; sample[2][3][4]=1; sample[2][3][5]=1; sample[2][3][6]=1; sample[2][3][7]=1; sample[2][3][8]=0;

 	sample[3][0][0]=0; sample[3][0][1]=1; sample[3][0][2]=0; sample[3][0][3]=1; sample[3][0][4]=1; sample[3][0][5]=1; sample[3][0][6]=0; sample[3][0][7]=0; sample[3][0][8]=0;
 	sample[3][1][0]=0; sample[3][1][1]=1; sample[3][1][2]=0; sample[3][1][3]=0; sample[3][1][4]=1; sample[3][1][5]=1; sample[3][1][6]=0; sample[3][1][7]=1; sample[3][1][8]=0;
 	sample[3][2][0]=0; sample[3][2][1]=0; sample[3][2][2]=0; sample[3][2][3]=1; sample[3][2][4]=1; sample[3][2][5]=1; sample[3][2][6]=0; sample[3][2][7]=1; sample[3][2][8]=0;
 	sample[3][3][0]=0; sample[3][3][1]=1; sample[3][3][2]=0; sample[3][3][3]=1; sample[3][3][4]=1; sample[3][3][5]=0; sample[3][3][6]=0; sample[3][3][7]=1; sample[3][3][8]=0;

 	sample[4][0][0]=0; sample[4][0][1]=1; sample[4][0][2]=0; sample[4][0][3]=0; sample[4][0][4]=1; sample[4][0][5]=0; sample[4][0][6]=0; sample[4][0][7]=1; sample[4][0][8]=0;
 	sample[4][1][0]=0; sample[4][1][1]=0; sample[4][1][2]=0; sample[4][1][3]=1; sample[4][1][4]=1; sample[4][1][5]=1; sample[4][1][6]=0; sample[4][1][7]=0; sample[4][1][8]=0;
 	sample[4][2][0]=0; sample[4][2][1]=1; sample[4][2][2]=0; sample[4][2][3]=0; sample[4][2][4]=1; sample[4][2][5]=0; sample[4][2][6]=0; sample[4][2][7]=1; sample[4][2][8]=0;
 	sample[4][3][0]=0; sample[4][3][1]=0; sample[4][3][2]=0; sample[4][3][3]=1; sample[4][3][4]=1; sample[4][3][5]=1; sample[4][3][6]=0; sample[4][3][7]=0; sample[4][3][8]=0;



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
         delayMS(100);

         if (newAccelData()) {
             acc = getAccelData();
             printf("%d\n", acc.x);
           }

/*
         if (getGUID() == 11){
           bigShaq = 1;
           setColor(YELLOW);
         }
*/
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

      delayMS(100);


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
        //printf("%d,(%d;%d)\n",(int)getGUID(),position[0],position[1]);

      return 1;
}




 byte backMessageHandler(void) {
   if (thisChunk==NULL) return 0;
   uint8_t sender=faceNum(thisChunk);

   delayMS(100);

   if ( (sender==xplusBorder && thisChunk->data[0] == (position[0]+1) && thisChunk->data[1] == position[1]) ||
        (sender==5-xplusBorder && thisChunk->data[0] == (position[0]-1) && thisChunk->data[1] == position[1]) ||
        (sender==yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]+1)) ||
        (sender==5-yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]-1)) ) {

   nbreWaitedAnswers--;

   if (nbreWaitedAnswers==0 && lien == UNKNOWN){
     setColor(YELLOW);
   }



   if (nbreWaitedAnswers==0 && lien != UNKNOWN) {

     setColor(AQUA);
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

    delayMS(50);

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

      //printf("%d, (%d;%d), %d, %d\n",(int)getGUID(),receiver[0],receiver[1], donnee, fonc);

      delayMS(50);

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

      if (fonc == 166)
        spawner();
    /*else if (fonc == 166 && donnee != 0)
        spawner(donnee)*/
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
  delayMS(50);
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

  if (bigShaq == UNKNOWN){

  if (thisNeighborhood.n[UP] != VACANT)
  miseAZero(UP);
  AccelData acc = getAccelData();
  if (acc.x >= 7)
    fpos = 0;
  else if (acc.x <= -7)
    fpos = 2;
  else
    fpos = 1;


  if (position[0] == posSpawner[0] && position[1] == posSpawner[1]){
    forme = rand() % 5;
    rota = rand() % 4;
  }

  if (sample[forme][rota][fpos] == 1)
    setColor(YELLOW);
  else
    setColor(WHITE);

      for (uint8_t p=0; p<6; p++) {
          if (thisNeighborhood.n[p] != VACANT)
              sendShape(p);
      }
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


      if (fpos != thisChunk->data[2] && bigShaq != 1){
        forme = thisChunk->data[0];
        rota = thisChunk->data[1];
        fpos = thisChunk->data[2];



        if (sample[forme][rota][fpos] == 1)
          setColor(YELLOW);
				else
					setColor(WHITE);


        for (uint8_t p=0; p<6; p++) {
          if (p!=sender && thisNeighborhood.n[p] != VACANT){

            sendShape(p);
          }
        }

              blockedCheck(DOWN);

              if (fpos == 4){
                scrollTimeout.callback = (GenericHandler)(&spawner);
                scrollTimeout.calltime = getTime() + 3000;
                registerTimeout(&scrollTimeout);
              }

              if (fpos == 0 || fpos == 1 || fpos == 2){
                if (thisNeighborhood.n[UP] != VACANT)
                miseAZero(UP);
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

  setColor(AQUA);
  fpos = UNKNOWN;

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


byte blockedCheck (PRef p){

if (sample[forme][rota][fpos] == 1){

    if (thisNeighborhood.n[p] != VACANT)
      areYouBlocked(p);

    else if (thisNeighborhood.n[p] == VACANT){
      for (uint8_t p=0; p<6; p++) {
        if (thisNeighborhood.n[p] != VACANT){

          sendStop(p);
        }
      }
    }
  }

  return 1;
}


byte blockedFinal (void){
  if (thisChunk == NULL) return 0;
    byte voisinState = thisChunk->data[0];

    if (voisinState == 1){

      for (uint8_t p=0; p<6; p++) {
        if (thisNeighborhood.n[p] != VACANT)
          sendStop(p);
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


  if (fpos <= 8 && fpos >= 0 && bigShaq != 1){
    if (sample[forme][rota][fpos] == 1)
      bigShaq = 1;

    if (fpos == 1 && (posSpawner[1] - position[1]) >= 3){
      delayMS(1000);
      sendExecOn(UP, posSpawner[0], posSpawner[1], 0, 166);
    }
    /*else if (fpos == 1 && (posSpawner[1] - position[1]) < 3) {
      delayMS(1000);
      sendExecOn(UP, posSpawner[0], , 0, 166);
    }*/

    fpos = UNKNOWN;
    forme = UNKNOWN;
    rota = UNKNOWN;


    for (uint8_t p=0; p<6; p++) {
      if (p != sender && thisNeighborhood.n[p] != VACANT){
        sendStop(p);
      }
    }
  }

  return 1;
}
