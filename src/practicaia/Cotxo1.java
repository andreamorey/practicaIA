/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

// Exemple de Cotxo molt bàsic
public class Cotxo1 extends Agent {

    static final boolean DEBUG = true;
    static final int ESQUERRA = 0;
    static final int CENTRAL = 1;
    static final int DRETA = 2;
    static final int COTXE = 1;
    static final int PARED = 0;

    int numDreta = 0;
    int numEsquerra = 0;

    int VELOCITATTOPE = 5;
    int VELOCITATFRE = 4;

    Estat estat;
    int espera = 0;
    double desquerra, ddreta, dcentral;

    boolean aDreta; //(true si la distancia mes curta es la taca de la dreta)
    double distancia;
    double distanciaMin;

    public Cotxo1(Agents pare) {
        super(pare, "Cotxo", "imatges/CotxoV.png");
    }

    @Override
    public void inicia() {
        setAngleVisors(40);
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
        setVelocitatAngular(9);
        ddreta = estat.distanciaVisors[DRETA];
        desquerra = estat.distanciaVisors[ESQUERRA];
        dcentral = estat.distanciaVisors[CENTRAL];
        numDreta = 0;
        numEsquerra = 0;

        if (estat.revolucions > 2500 && estat.marxa < 5 && dcentral > 200) {
            endavant(estat.marxa + 1);
        } else {
            endavant(estat.marxa);
            espera = 1;  // esto que es?
        }

        for (int i = 0; i < estat.numObjectes; i++) {
            if (estat.objectes[i].tipus == Agent.TACAOLI) {
                distancia = estat.posicio.distancia(estat.objectes[i].posicio);
                if (distancia < 100) {
                    //System.out.println("Distancia taca oli " + distancia);
                    if (distancia < distanciaMin) {
                        distanciaMin = distancia;
                    }
                    if (estat.objectes[i].sector >= 3) {
                        numDreta++;
                        if (distancia > distanciaMin) {
                            aDreta = true;
                        }
                    } else if (estat.objectes[i].sector <= 2 && estat.objectes[i].sector > 0) {
                        numEsquerra++;
                        if (distancia == distanciaMin) {
                            aDreta = false;
                        }
                    }
                }
            }
        }
        if (numDreta > 0 || numEsquerra > 0) {
            if (aDreta) {
                esquerra();
            } else {
                dreta();
            }
        }
        if (dcentral > 160 && (desquerra > 20) && (ddreta > 20)) {
            noGiris();

        } else {
            if (estat.marxa >= VELOCITATFRE) {
                endavant(estat.marxa - 1);
                espera = 1;
            }
            //if (ddreta != desquerra) {
            // setVelocitatAngular(8);

            if (ddreta > desquerra) {
                if (numDreta > 0 && aDreta) {
                    esquerra();
                } else {
                    dreta();
                }
            } else {
                if (numEsquerra > 0 && !aDreta) {
                    dreta();
                } else {
                    esquerra();
                }
            }
            //}
        }

        if (dcentral > 170) {
            //endavant(VELOCITATTOPE);
            setVelocitatAngular(6);
        } else {
            setVelocitatAngular(9);
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
            if ((estat.distanciaVisors[CENTRAL] > 15) && (estat.distanciaVisors[ESQUERRA] > 15) && (estat.distanciaVisors[DRETA] > 15)) {
                endavant(1);
            } else { //cocha por delante

                //if (estat.objecteVisor[CENTRAL] == PARED || estat.objecteVisor[ESQUERRA] == PARED || estat.objecteVisor[DRETA] == PARED){
                espera = 30;
                noGiris();
                enrere(2);
            }
        }

        //detecta coche con visor central y dispara
        if (estat.objecteVisor[CENTRAL] == COTXE) {
            dispara();
        }
    }

}