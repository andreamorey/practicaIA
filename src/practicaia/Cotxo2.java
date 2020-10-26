/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;


public class Cotxo2 extends Agent {
 static final boolean DEBUG = true;
    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int COTXE = 1;
    static final int PARED = 0;

    int VELOCITATTOPE = 5;
    int VELOCITATFRE = 4;

    Estat estat;
    int espera = 0;
    double desquerra, ddreta, dcentral;
    double distanciaObj, distanciaVis;
    int altreCotxe;

    public Cotxo2(Agents pare) {
        super(pare, "Rival", "imatges/cotxe2.gif");
    }

    @Override
    public void inicia() {
        setAngleVisors(10);
        setDistanciaVisors(350);
        setVelocitatAngular(9);
    }

    @Override
    public void avaluaComportament() {

        estat = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        if (espera > 0) {  // no facis res, continua amb el que estaves fent
            espera--;
            return;
        }

        ddreta = estat.distanciaVisors[DRETA];
        desquerra = estat.distanciaVisors[ESQUERRA];
        dcentral = estat.distanciaVisors[CENTRAL];

        setVelocitatAngular(9);

        if (dcentral > 200) {
            setAngleVisors(10);
        } else {
            setAngleVisors(40);
        }

        if (estat.revolucions > 2500 && estat.marxa < 5 && dcentral > 200) {
            endavant(estat.marxa + 1);

        } else {
            endavant(estat.marxa);

        }

        if (dcentral > 200 && (desquerra > 30) && (ddreta > 30)) {
            noGiris();
        } else {
            if (estat.marxa > VELOCITATFRE) {
                endavant(estat.marxa - 1);

            }
            
            if (ddreta > desquerra) {
                dreta();
            } else {
                esquerra();
            }
        }

        // no ir en contra dirección :)
        if (estat.contraDireccio) {
            espera = 30;
            setVelocitatAngular(5);
            if (ddreta > desquerra) {
                dreta();
            } else {
                esquerra();
            }
        }

        // cuando choca con coche o pared
        if (estat.enCollisio) {
            //choca por detras
            if ((estat.distanciaVisors[CENTRAL] > 15)
                    && (estat.distanciaVisors[ESQUERRA] > 15)
                    && (estat.distanciaVisors[DRETA] > 15)) {
                endavant(1);
                //choca por la izquierda
            } else if ((estat.distanciaVisors[CENTRAL] > 15)
                    && (estat.distanciaVisors[ESQUERRA] <= 15)
                    && (estat.distanciaVisors[DRETA] > 15)) {
                dreta();
                endavant(1);
                espera = 5;
                //choca por la derecha
            } else if ((estat.distanciaVisors[CENTRAL] > 15)
                    && (estat.distanciaVisors[ESQUERRA] > 15)
                    && (estat.distanciaVisors[DRETA] <= 15)) {
                esquerra();
                endavant(1);
                espera = 5;
                
            } else { //choca por delante
                espera = 30;
                noGiris();
                enrere(2);

            }
            return;
        }

        //detecta coche con visor central y dispara
        if (estat.objecteVisor[CENTRAL] == COTXE) {
            dispara();
        }

        //esquivar taques oli
        if (estat.distanciaVisor == 10){
            distanciaVis = 90;
        } else if (estat.distanciaVisor == 40){
            distanciaVis = 30;
        }
        
        for (int i = 0; i < estat.numObjectes; i++) {
            if (estat.objectes[i].tipus == Agent.TACAOLI) {
                distanciaObj = estat.posicio.distancia(estat.objectes[i].posicio);
                if (distanciaObj < 100) {
                    if (estat.objectes[i].sector == 2 && estat.distanciaVisors[ESQUERRA] >= distanciaVis) {
                        esquerra();
                    } else if (estat.objectes[i].sector == 2 && estat.distanciaVisors[ESQUERRA] < distanciaVis) {
                        dreta();
                    } else if (estat.objectes[i].sector == 3 && estat.distanciaVisors[DRETA] >= distanciaVis) {
                        dreta();
                    } else if (estat.objectes[i].sector == 3 && estat.distanciaVisors[DRETA] < distanciaVis) {
                        esquerra();
                    }
                }
                //coger gasolina si queda poca
            } else if (estat.objectes[i].tipus == Agent.RECURSOS && estat.fuel < 2000) {
                distanciaObj = estat.posicio.distancia(estat.objectes[i].posicio);
                if (distanciaObj < 100) {
                    if (estat.objectes[i].sector == 1) {
                        dreta();
                    } else if (estat.objectes[i].sector == 4) {
                        esquerra();
                    }
                }
            }
        }
        
        //soltar taques oli
        //soltar taques oli
            altreCotxe = (estat.id + 1) % estat.numBitxos;
            // si el otro coche me está viendo
            if (estat.veigEnemic[altreCotxe] && estat.oli > 0){
                posaOli();
            }
    }

/*


    static final boolean DEBUG = true;

    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int COTXE = 1;
    
    int VELOCITATTOPE = 5;
    int VELOCITATFRE = 3;

    Estat estat;
    int espera = 0;

    double desquerra, ddreta, dcentral;


    public Cotxo2(Agents pare) {
        super(pare, "Rival", "imatges/cotxe2.gif");
    }

    @Override
    public void inicia() {
        endavant(1);
        setAngleVisors(40);
        setDistanciaVisors(350);
        setVelocitatAngular(9);
    }

    @Override
    public void avaluaComportament() {

        estat = estatCombat();  // Recuperam la informació actualitzada de l'entorn

        // Si volem repetir una determinada acció durant varies interaccions
        // ho hem de gestionar amb una variable (per exemple "espera") que faci
        // l'acció que volem durant el temps que necessitem
        
        if (espera > 0) {  // no facis res, continua amb el que estaves fent
            espera--;
            return;
        } else {
                  
            if (estat.enCollisio && estat.distanciaVisors[CENTRAL] < 15) // evita fer-ho marxa enrera
            {
                noGiris();

                if (estat.distanciaVisors[CENTRAL] > 20) {
                    endavant(4);
                    return;
                }

                enrere(4);
                espera = 30;
                return;
            }

            ddreta = estat.distanciaVisors[DRETA];
            desquerra = estat.distanciaVisors[ESQUERRA];
            dcentral = estat.distanciaVisors[CENTRAL];

            if (dcentral > 170) {
                endavant(VELOCITATTOPE);
                setVelocitatAngular(6);
            }else{
                setVelocitatAngular(9);
            }
            
            if (estat.objecteVisor[CENTRAL] == COTXE)
            {
                dispara();
            }

            // Per si vull anar el més recte possible: no sempre és la manera més ràpida
            if ((desquerra > 40) && (ddreta > 40) && dcentral > 180) {
                endavant(VELOCITATTOPE);
                noGiris();
                return;
            }

            setVelocitatAngular(9);
            if (ddreta > desquerra) {
                dreta();
            } else {
                esquerra();
            } 
            
            endavant(4);
        }
    }
*/
}

