#include "block.bbh"
#include "clock.bbh"

/*

 #ifdef LOG_DEBUG
 #include "log.bbh"
 #endif

*/
threaddef  #define NO_LIEN  99

threadvar byte position[2];

threadvar  uint8_t lien;
threadvar  uint8_t nbreWaitedAnswers;
threadvar byte xplusBorder;
threadvar byte yplusBorder;


threaddef  #define MYCHUNKS 12
threadextern Chunk* thisChunk;
threadvar  Chunk myChunks[MYCHUNKS];



threadvar uint8_t myNeighborhood[6];

 /***************/
 /** functions **/
 byte goMessageHandler(void);
 byte sendBackChunk(PRef p);
 byte backMessageHandler(void);
 byte sendCoordChunk(PRef p);
 byte coordMessageHandler(void);
 void neighborChangeDetect(void);
 void getNeighbors(void);


/******************************/
 void myMain(void) {
     // We are forced to use a small delay before program execution,
   delayMS(5000);
   lien=NO_LIEN;
   nbreWaitedAnswers=0;
   position[0] = 0;
   position[1] = 0;
   xplusBorder = WEST;
   yplusBorder = UP;

   getNeighbors();

   if (thisNeighborhood.n[DOWN] == VACANT && thisNeighborhood.n[EAST] == VACANT) {

          setColor(RED);
          position[0]=127;
          position[1]=127;
          delayMS(2000);

          for (uint8_t p=0; p<6; p++) {
            if (thisNeighborhood.n[p] != VACANT) {
               sendCoordChunk(p);
                 nbreWaitedAnswers++;
             }
         }
     }



 while(1) {
         delayMS(200);
         if (position[0] == 127 && position[1] == 127 && nbreWaitedAnswers == 0)
          setColor(YELLOW);
          if (position[0] == 130 && position[1] == 131 && nbreWaitedAnswers == 0){
            setColor(PURPLE);
            delayMS(100);
            setColor(WHITE);
          }

        //if (nbreWaitedAnswers != 0)
          //printf("%d, %d\n",getGUID(), nbreWaitedAnswers);
   }
}


/******************/
/**** systeme ****/
/******************/

void userRegistration(void) {
    registerHandler(SYSTEM_MAIN, (GenericHandler)&myMain);
    registerHandler(EVENT_NEIGHBOR_CHANGE, (GenericHandler)&neighborChangeDetect);
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

      delayMS(2000);


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

        //delayMS(1000);


      if (nbreWaitedAnswers==0 && lien != NO_LIEN){
          sendBackChunk(lien);
        }
        printf("%d,(%d;%d)\n",(int)getGUID(),position[0],position[1]);

      /*  if (getGUID() == 2 || getGUID() == 5 || getGUID() == 31 || getGUID() == 35)
        printf("%d,(%d;%d)\n",(int)getGUID(),position[0],position[1]);*/

      return 1;
}




 byte backMessageHandler(void) {
   if (thisChunk==NULL) return 0;
   uint8_t sender=faceNum(thisChunk);
   delayMS(2000);

   if (nbreWaitedAnswers != 0){
   if ( (sender==xplusBorder && thisChunk->data[0] == (position[0]+1) && thisChunk->data[1] == position[1]) ||
        (sender==5-xplusBorder && thisChunk->data[0] == (position[0]-1) && thisChunk->data[1] == position[1]) ||
        (sender==yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]+1)) ||
        (sender==5-yplusBorder && thisChunk->data[0] == position[0] && thisChunk->data[1] == (position[1]-1)) ) {

   nbreWaitedAnswers--;
   //printf("%d, Reponses = %d, Envoyeur: %d\n",(int)getGUID(), nbreWaitedAnswers, sender);

   if (nbreWaitedAnswers==0 && lien != NO_LIEN) {

       setColor(AQUA);
       sendBackChunk(lien);
        }
    }
 //delayMS(1000);
   return 1;
 }
}




//*** +++ ***/

void neighborChangeDetect(void){


 if (position[0]!=0 && position[1]!=0){

   delayMS(200);


      if (thisNeighborhood.n[UP]!=VACANT && myNeighborhood[UP]==0) //si on "decouvre" un nvx voisin a droite on lui attribue des coord
{
  printf("nvx en haut\n ");
  sendCoordChunk(UP);}

else if(thisNeighborhood.n[WEST]!=VACANT && myNeighborhood[WEST]==0) //si on "decouvre" un nvx voisin en haut
{
  sendCoordChunk(WEST);
  printf("nvx a droite\n");
}
}
}


 void getNeighbors(void){


   uint8_t c;
   c=0;


   for (uint8_t p=0; p<6; p++) {
    if (thisNeighborhood.n[p] != VACANT)
    { myNeighborhood[p]=1;
    c++;}
    else
    {myNeighborhood[p]=0;}


  }
  printf(" %d voisins sur %d \n",c,(int)getGUID());


 }
