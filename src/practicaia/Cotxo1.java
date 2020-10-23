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

    int VELOCITATTOPE = 5;
    int VELOCITATFRE = 3;

    Estat estat;
    int espera = 0;
    double desquerra, ddreta, dcentral;
    
    int espera2;

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
        
        
        
        
        

        // Si volem repetir una determinada acció durant varies interaccions
        // ho hem de gestionar amb una variable (per exemple "espera") que faci
        // l'acció que volem durant el temps que necessitem

        if (espera > 0) {  // no facis res, continua amb el que estaves fent
            espera--;
            return;
        } else {

            ddreta = estat.distanciaVisors[DRETA];
            desquerra = estat.distanciaVisors[ESQUERRA];
            dcentral = estat.distanciaVisors[CENTRAL];
            

            if (estat.enCollisio && estat.veigAlgunEnemic) {
               // System.out.println("Me he chocado");
                enrere(1);
                setVelocitatAngular(3);
//                if (estat.sector)
//                if (ddreta > desquerra) {
//                    esquerra();
//                } else {
//                    dreta();
//                }
                espera = 20;
                return;
            }

    //esquivar el coche cuando esta cerca
            //System.out.println("posicio enemic: " + estat.posicioEnemic[1]);
            if (estat.veigAlgunEnemic && estat.posicio.distancia(estat.posicioEnemic[1]) < 150){
                System.out.println("Posicio enemic  " + estat.posicio.distancia(estat.posicioEnemic[1]));
                if (ddreta > desquerra) {
                    dreta();
                } else {
                    esquerra();
                }
                if (estat.marxa < 5) {
                        if (estat.revolucions > 2000) {
                            endavant(estat.marxa + 1);
                        } else {
                            endavant(estat.marxa);
                        }
                    }
            }
                // en contra direcció
//            if (estat.contraDireccio && vaEndavant()){
//                
//                espera = 30;
//            }
            if (estat.enCollisio && estat.distanciaVisors[CENTRAL] < 15) // evita fer-ho marxa enrera
            {
                noGiris();

                if (estat.distanciaVisors[CENTRAL] > 20) {
                    if (estat.marxa < 5) {
                        if (estat.revolucions > 1500) {
                            endavant(estat.marxa + 1);
                        } else {
                            endavant(estat.marxa);
                        }
                    }
                    //endavant(4);
                    return;
                }
                enrere(1);
                espera = 30;
                return;
            }

            ddreta = estat.distanciaVisors[DRETA];
            desquerra = estat.distanciaVisors[ESQUERRA];
            dcentral = estat.distanciaVisors[CENTRAL];

            if (dcentral > 400) {   //está en una recta
                //endavant(VELOCITATTOPE); 
                if (estat.marxa < 5) {
                    if (estat.revolucions > 1500) {
                        endavant(estat.marxa + 1);
                    } else {
                        endavant(estat.marxa);
                    }
                }
            }

            if (estat.objecteVisor[CENTRAL] == COTXE) //detecta coche con visor central
            {
                dispara();
            }

            // Per si vull anar el més recte possible: no sempre és la manera més ràpida
            if ((desquerra > 40) && (ddreta > 40) && dcentral > 210) {
                //endavant(VELOCITATTOPE);
                if (estat.marxa < 5) {
                    if (estat.revolucions > 1500) {
                        endavant(estat.marxa + 1);
                    } else {
                        endavant(estat.marxa);
                    }
                }
                noGiris();
                return;
            }
            if (dcentral < 210 && estat.marxa > VELOCITATFRE) {

                endavant(estat.marxa - 1);
            }

            if (estat.objecteVisor[CENTRAL] == 0 || estat.objecteVisor[ESQUERRA] == 0 || estat.objecteVisor[DRETA] == 0) {
                if (ddreta > desquerra) {
                    dreta();
                } else {
                    esquerra();
                }
            }

            //endavant(4);
            //endavant(VELOCITATFRE);
            if (estat.marxa > 4 && estat.revolucions < 1500) {
                if (ddreta < 20 || desquerra < 20) {
                    endavant(estat.marxa + 1);
                } else {
                    endavant(estat.marxa - 1);
                }
            }

        }

    }
}
