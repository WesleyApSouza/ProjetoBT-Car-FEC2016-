#include <SoftwareSerial.h>
#include<Servo.h>

#define PinoVelocidade 3 //Ligado ao pino 1 do L293D  
#define Entrada1 2 //Ligado ao pino 2 do L293D  
#define Entrada2 7 //Ligado ao pino 7 do L293D  
#define setaS 12
#define setaD 8
#define farol 4
#define luzRe 11
#define buzina 13

SoftwareSerial leBluetooth(14 , 15); // rx , tx
Servo meuServo;
int v = 0;
int estado = 90;
int valor = LOW;
int lr = 0;

long previousMillis = 0;
long interval = 1000;

void re(String s) {

  s.remove(0, 1); // Retira o primeiro elemento que é uma letra R(re) deixando apenas uma sequencia de valores numericos
  v = s.toInt();
  analogWrite(PinoVelocidade, v);
  digitalWrite(Entrada1, HIGH);
  digitalWrite(Entrada2, LOW);

}

void acelerar(String s) {
  s.remove(0, 1);
  v = s.toInt();
  analogWrite(PinoVelocidade, v);
  digitalWrite(Entrada1, LOW);
  digitalWrite(Entrada2, HIGH);

}
void parar(){
  analogWrite(PinoVelocidade,0);
  digitalWrite(Entrada1, LOW);
  digitalWrite(Entrada2, LOW);
}

void virar(String s) {
  s.remove(0, 1); // Igual a function re(); e acelerara();  porem a letra é V (Volante)
  estado = s.toInt();

  meuServo.write(estado);// Faz com que o servo motor vire entre 0º a 180º, de acordo com o dado recebido
}

String leitura() {
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

  return string; // string == primeiro elemento um letra R,D ou V, seguindo de uma sequencia de valores numericos entre 0 a 255 para R e D e de 0 a 180 para V
}
void preparaVariaveis() {
  pinMode(setaS, OUTPUT);
  pinMode(setaD, OUTPUT);
  pinMode(farol, OUTPUT);
  pinMode(luzRe, OUTPUT);
  pinMode(buzina, OUTPUT);
  meuServo.attach(10);
}
void piscaSetas(){
  unsigned long atualMillis = millis();
  if (estado != 90) {

    if (atualMillis - previousMillis > interval) {
      previousMillis = atualMillis;

      if (estado > 90) {
        digitalWrite(setaD, LOW);
        if (valor == LOW)
          valor = HIGH;
        else
          valor = LOW;
        digitalWrite(setaS, valor);

      } else if (estado < 90) {
        digitalWrite(setaS, LOW);
        if (valor == LOW) {
          valor = HIGH;

        } else
          valor = LOW;
        digitalWrite(setaD, valor);
      } else {
        digitalWrite(setaD, LOW);
        digitalWrite(setaS, LOW);
      }
    }
  }
  if (estado == 90 && valor == 1) {
    digitalWrite(setaD, LOW);
    digitalWrite(setaS, LOW);
  } 
}

void processamento(String dado) {
  char aux = dado.charAt(0);

  if (aux == 'R') { // Dar Ré
    digitalWrite(luzRe, HIGH);
    re(dado);
  } else if (aux == 'D') { // ir para frente
    if(lr == 0){
        digitalWrite(luzRe, LOW);
    }else{
      analogWrite(luzRe,30);
    }
    acelerar(dado);
  } else if (aux == 'V') { // virar
    virar(dado);
  } else if(dado.equals("P")){
     parar();
  }else if (dado.equals("FT")) { // farol Ligado
    lr = 1;
    digitalWrite(farol, HIGH);
    analogWrite(luzRe,30);
  } else if (dado.equals("FF")) { // farol desligado
    lr = 0;
    digitalWrite(farol, LOW);
    digitalWrite(luzRe,LOW);
  } else if(dado.equals("BT")){
    tone(buzina, 200);
  }else if(dado.equals("BF")){
    noTone(buzina);
  }
}

void setup() {
  Serial.begin(9600);
  leBluetooth.begin(9600);
  preparaVariaveis(); // Configura entras e saidas do Arduino
}

void loop() {
  String dado = leitura(); // Efetua a leitura dos dados enviados via Bluetooth
  processamento(dado); // Processa os dado recebido e executa o comando definido
  piscaSetas();
}
