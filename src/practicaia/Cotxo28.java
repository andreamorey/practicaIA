/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package agents;

// Exemple de Cotxo molt bàsic
public class Cotxo28 extends Agent {

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

    public Cotxo28(Agents pare) {
        super(pare, "Cotxo28", "imatges/amarillo.PNG");
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

        altreCotxe = (estat.id + 1) % estat.numBitxos;
        if (espera > 0) {  // no facis res, continua amb el que estaves fent
            espera--;
            return;
        }

        ddreta = estat.distanciaVisors[DRETA];
        desquerra = estat.distanciaVisors[ESQUERRA];
        dcentral = estat.distanciaVisors[CENTRAL];

        if (dcentral > 250) {
            setAngleVisors(10);
        } else {
            setAngleVisors(40);
        }

        if (estat.revolucions > 2500 && estat.marxa < 5 && dcentral > 200) {
            endavant(estat.marxa + 1);

        } else {
            endavant(estat.marxa);

        }

        if (estat.angleVisors == 10) {
            distanciaVis = 180;
        } else if (estat.angleVisors == 40) {
            distanciaVis = 75;
        }
        
        // cuando choca con coche o pared
        if (estat.enCollisio) {
            //choca por detras
            if ((estat.distanciaVisors[CENTRAL] > 20)
                    && (estat.distanciaVisors[ESQUERRA] > 20)
                    && (estat.distanciaVisors[DRETA] > 20)) {
                atura();
                endavant(2);
                //choca por la izquierda
//            } else if ((estat.distanciaVisors[CENTRAL] > 15)
//                    && (estat.distanciaVisors[ESQUERRA] <= 15)
//                    && (estat.distanciaVisors[DRETA] > 15)) {
//                dreta();
//                endavant(1);
//                espera = 5;
//                //choca por la derecha
//            } else if ((estat.distanciaVisors[CENTRAL] > 15)
//                    && (estat.distanciaVisors[ESQUERRA] > 15)
//                    && (estat.distanciaVisors[DRETA] <= 15)) {
//                esquerra();
//                endavant(1);
//                espera = 5;

            } else { //choca por delante
                espera = 20;
                noGiris();
                enrere(2);

            }
            return;
        }
        
        if (dcentral > 180 && (desquerra > distanciaVis) && (ddreta > distanciaVis)) {
            noGiris();
        } else {
            if (dcentral > 180) {
                setVelocitatAngular(7);
                endavant(estat.marxa);
            } else {
                setVelocitatAngular(9);
                if (estat.marxa > VELOCITATFRE) {
                    endavant(estat.marxa - 1);
                }
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

        

        //detecta coche con visor central y dispara
        if (estat.objecteVisor[CENTRAL] == COTXE) {
            dispara();

        }

        //esquivar coche si está cerca
        if (estat.veigAlgunEnemic && estat.posicio.distancia(estat.posicioEnemic[altreCotxe]) < 100) {
            if (estat.sector[altreCotxe] == 2) {
                esquerra();
            } else if (estat.sector[altreCotxe] == 3) {
                dreta();
            }
        }

        if (estat.angleVisors == 10) {
            distanciaVis = 90;
        } else if (estat.angleVisors == 40) {
            distanciaVis = 30;
        }
        //esquivar taques oli
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

        //soltar taques oli (no va de momento)
        // si el otro coche me está viendo
        if (estat.veigEnemic[altreCotxe] && estat.oli > 0) {
            posaOli();
        }

    }
}