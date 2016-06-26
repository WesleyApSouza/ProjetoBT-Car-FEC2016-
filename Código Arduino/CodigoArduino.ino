#include <AFMotor.h>
#include<SoftwareSerial.h>
#include<Servo.h>

SoftwareSerial leBluetooth(14, 15); // rx,tx
AF_DCMotor motor1(1);
Servo meuServo;
int v = 0;
int velo = 0;
int cmd = 0;

void re(String s){
  
  s.remove(0, 1); // Retira o primeiro elemento que é uma letra R(re) deixando apenas uma sequencia de valores numericos
  v = s.toInt();
  velo = v *25;
  cmd = 1;
  motor1.setSpeed(velo); //Os valores para R variam entre 0 a 100, a potencia maxima do motor e 255. por isso a multiplicação por 25
  motor1.run(BACKWARD);
  
}

void acelerar(String s){ 
   s.remove(0, 1);
   v = s.toInt();
   velo = v * 25; 
   cmd = 2;
   motor1.setSpeed(velo); // Igual a function re();
   motor1.run(FORWARD);
  
}

void virar(String s){
   s.remove(0, 1); // Igual a function re(); e acelerara();  porem a letra é V (Volante)
   v = s.toInt();
   meuServo.write(v);// Faz com que o servo motor vire entre 0º a 180º, de acordo com o dado recebido
   motor1.setSpeed(velo);
   
   if(cmd == 0)
    motor1.run(RELEASE);
   else if(cmd == 1)
    motor1.run(BACKWARD);
   else
    motor1.run(FORWARD);
}

void prepararDispositivo(){
  leBluetooth.begin(9600);
  delay(400);  
  leBluetooth.flush();                 
  
}

String leitura(){
   char comando;
   String string = "";
   
   if (leBluetooth.available() > 0) {
    string = "";
  }
  while (leBluetooth.available() > 0) {
    comando = (byte)leBluetooth.read(); 
    if (comando == ':') {
      break;
    } else {
      string += comando; 
    }
    delay(1);
  }
  return string; // string == primeiro elemento um letra R,D ou V, seguindo de uma sequencia de valores numericos entre 0 a 100 para R e D e de 0 a 180 para V
}

void setup() {
  meuServo.attach(10);
  motor1.setSpeed(0);
  Serial.begin(9600);
  prepararDispositivo();
}

void loop() {
  
  String dado = leitura();
  char aux = dado.charAt(0);
  if (aux == 'R') { // Dar Ré
    re(dado);
  } else if (aux == 'D') { // ir para frente
   acelerar(dado);
  } else if (aux == 'V') { // virar
    virar(dado);
  }
  if(v == 0){
    motor1.run(RELEASE);
    velo = 0;
  }

}
