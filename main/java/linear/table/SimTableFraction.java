package linear.table;

import java.math.RoundingMode;
import java.util.Map;
import forAll.*;

public class SimTableFraction implements  SimTable {
    // заприватить все
    public Map<String,Integer> goal;
    public Fraction[][] table;
    public  Fraction[][] tableColumn;
    public  Fraction[][] koef ;
    public  Fraction[][] koefColumn;
    public Fraction f[];
    public Fraction b[];
    public  String [] base;
    public  String [] free;


    public Fraction mainElem;
    public  int mainRow;
    public  int mainColumn;


    public SimTableFraction(Fraction[][] table, String[] base, String[] free){
        setBase(base);
        setFree(free);
        setTable(table);
        // убрать сеты
    }




    /* Табличные действия */
    public void setTableColumn(Fraction[][] tableColumn) {
        this.tableColumn = new Fraction[tableColumn[0].length][tableColumn.length];

        for(int i=0; i< tableColumn[0].length; i++){
            for(int j=0; j < tableColumn.length; j++){
                this.tableColumn[i][j]=tableColumn[j][i];
            }
        }
    }


    public void setKoefColumn(Fraction[][] koef) {
        this.koefColumn = new Fraction[koef[0].length][koef.length];

        for(int i=0; i< koef[0].length; i++){
            for(int j=0; j < koef.length; j++){
                this.koefColumn[i][j]=koef[j][i];
            }
        }
    }

    public void setF(Fraction[] f) {
        this.f = new Fraction[f.length-1];
        if (f.length - 1 >= 0) System.arraycopy(f, 0, this.f, 0, f.length - 1);

    }

    public void setB(Fraction[][] table) {
        this.b= new Fraction[table.length-1];
        for(int i=0; i<table.length-1; i++){
            this.b[i]=table[i][table[0].length-1];
        }
    }

    public void setKoef(Fraction[][] table) {
        this.koef = new Fraction[table.length - 1][table[0].length - 1]; // if ???
        for (int i = 0; i < table.length - 1; i++) {
            System.arraycopy(table[i], 0, koef[i], 0, table[0].length - 1);
        }
    }

    public void setAll(Fraction[][] table){
        setKoef(table);
        setKoefColumn(this.koef);
        setF(table[table.length-1]);
        setB(table);
        setTableColumn(table);
    }
    public void setTable(Fraction[][] table) {
        this.table = table;
        setAll(table);
    }



    public void setBase(String[] base) {
        this.base = base;
    }


    public void setFree(String[] free) {
        this.free = free;
    }

    // Методы!


    // MainElem
    public void mainElem() { // главный элемент возращать
        min(this.f);
        // goto
        minPositive(this.b, this.koefColumn[this.mainColumn]);

    }

    public boolean min(Fraction[] arr) { // колонку
        Fraction min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if(min.compareTo(arr[i]) > 0) {
                min = arr[i];
                index = i;
            }
        }

        mainColumn = index;
        if(min.compareTo(Fraction.valueOf(0)) < 0){
            return true;}
        else return false;
    }

    // надо ли B передавать в параметры?

    public void minPositive(Fraction[] b, Fraction[] arr){ // строку
        Fraction elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(
                    arr[i].compareTo(Fraction.valueOf(0)) >0 &&(
                            b[index].divide(elem, 5, RoundingMode.CEILING)
                                    .compareTo(b[i].divide(arr[i], 4, RoundingMode.CEILING)) >0
                                    || elem.compareTo(Fraction.valueOf(0))<0)

            ) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem;
        mainRow = index;
    }


    // changeTable


    public  void changeName(){
        String a = this.base[this.mainRow];
        this.base[this.mainRow] =
                this.free[this.mainColumn];
        this.free[this.mainColumn] = a;

    }

    void changeTable(){
        Fraction[][] arr = new Fraction[this.table.length][this.table[0].length];
        for(int i =0; i<this.table.length;i++){
            for(int j =0; j<this.table[0].length;j++){
                arr[i][j] = this.table[i][j];
            } }

// убрать отдельные методы для каждого действия
        for (int i = 0; i < this.table.length; i++) {
            for (int j = 0; j < this.table[0].length; j++) {

                if(i==this.mainRow) {
                    changeMainRow(i, j); //  6 ненужных инициализаций  и одна ненужная ссылка
                } else {

                    if (j == this.mainColumn) {
                        changeMainColumn(i, j);

                    } else {
                        changeOthers(i, j, arr);
                    } } } }

        changeName();

        // вызвать чэндч мэйнров
        this.table[this.mainRow][this.mainColumn] = this.table[this.mainRow][this.mainColumn].
                divide(mainElem, 5, RoundingMode.CEILING);
        this.setAll(this.table);
    }


    void changeMainRow(int i, int j){
        this.table[this.mainRow][j] = this.table[this.mainRow][j].divide(mainElem, 5, RoundingMode.CEILING);

    }

    void changeMainColumn(int i, int j){
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].divide(mainElem, 5, RoundingMode.CEILING);
        this.table[i][this.mainColumn] = this.table[i][this.mainColumn].multiply(Fraction.valueOf(-1));

    }

    void changeOthers (int i, int j, Fraction[][] arr){
        this.table[i][j] = // исправить на другое выражение
                this.mainElem.multiply(this.table[i][j]).subtract(
                        arr[this.mainRow][j].multiply(arr[i][this.mainColumn])).
                        divide(this.mainElem, 5, RoundingMode.CEILING);

    }

    // Result
    public void simplex(){
        while (min(this.f)) {
            mainElem();
            changeTable();
        }
    }


    /* methods.ArtificialBasis*/


    public void newSim(){

        int length =0;
        for(String e: this.free){
            if (e.charAt(0) == 'w')
                length++;
        }
        String [] free = this.free;
        int size= this.table.length;

        int size0= this.table[0].length;
        size0--;
        Fraction[][] table = new Fraction[size][size0];
        size--;
        int k=0;

        for (int i=0; i < size; i++){
            for(int j=0; j< size0; j++){
                if(free[j].charAt(0) != 'w'){
                    table[i][k] = this.table[i][j];
                    k++;
                }
                table[i][size0-1] = this.table[i][size];
            }
            k=0;
        }

        letters();
        this.setTable(table);
        this.table[size] = f(size,size0);
        this.setAll(this.table);
    }
    void letters(){ // оптимизировать метод
        // работает
        int lengthFree =0;
        for(String e: this.free){
            if (e.charAt(0) != 'w')
                lengthFree++;
        }

        int lengthBase =0;
        for(String e: this.base){
            if (e.charAt(0) != 'w')
                lengthBase++;
        }

        String[] free= new String[lengthFree];
        int i =0;
        for(String e: this.free) {
            if (e.charAt(0) != 'w') {
                free[i] = e;
                i++;
            }
        }

        i=0;
        String[] base = new String[lengthBase];
        for(String e: this.base){
            if (e.charAt(0) != 'w') {
                base[i] = e;
                i++;
            }
        }
        this.setBase(base);
        this.setFree(free);
    }

    Fraction[] f(int size, int size0){ // изменить все
        // оптимизировать
// строку b rjgbhyenm


        //!! интегер в bigdec исправить, ибо дорого валуеоф делать
        Fraction[]f = new Fraction[size];
        Fraction a = Fraction.valueOf(0);
        for(int i =0; i<size; i++) { // goal create
            if(i<(size-1) && goal.containsKey(this.free[i])) {
                a= a.subtract(Fraction.valueOf(goal.get(this.free[i])));

            }
            for(int j=0; j<size0; j++){
                if(j<(size0-1) && goal.containsKey(this.base[j]))
                    a= a.add(tableColumn[i][j].multiply(Fraction.valueOf(goal.get(this.base[j]))));
            }
            f[i]=a;

            a = Fraction.valueOf(0); // создать отдельно 0ую переменную
        }
        return f;
    }


    public void artificialBasis(){
        simplex();
        newSim();
        simplex();
    }


    /* methods.DualSimplex*/

    /* MainElem*/
    public void mainElemDual() { // главный элемент возращать
        min1(this.b);
        minPositive1(this.f, this.koef[mainRow]);

    }

    public boolean min1(Fraction[] arr) { // колонку
        Fraction min = arr[0];
        int index = 0;
        for (int i = 0; i < arr.length; i++) {
            if (
                    min.compareTo(arr[i])>0
            ) {
                min = arr[i];
                index = i;
            }
        }

        mainRow= index; //  и здесь
        return  min.compareTo(Fraction.valueOf(0))<0;
    }

    public void minPositive1(Fraction[] b, Fraction[] arr){ // сменить в названии b на  f
        Fraction elem = arr[0];
        int index = 0;
        for(int i = 0; i < arr.length; i++){
            if(
                    (arr[i].compareTo(Fraction.valueOf(0))<0 &&
                            (b[index].divide(elem, 5, RoundingMode.CEILING).compareTo(
                                    b[i].divide(arr[i], 5, RoundingMode.CEILING))>0))
                            || arr[index].compareTo(Fraction.valueOf(0))>0) {
                elem = arr[i];
                index = i;
            }
        }

        mainElem = elem; // вот здесь все портится
        mainColumn = index;  // и здесь
    }




    public void dualSimplex(){
        while (min1(b)) { // f on b
            mainElemDual();
            changeTable();
        }
    }


    public void gamory() {
        simplex();
        negative(table.length - 1);
      //  max(b);
         while(max(b)!=-1){
        // добавляем новое ограничение
        // преобразуем

        restrictions(); // новое ограничение
             dualSimplex();
    };
        negative(table.length - 1);
    }


    void negative(int index){ // сделать по индексу

     for(int i=0; i<table[0].length; i++){
         table[index][i].setNumenator(-table[index][i].getNumenator());
     }

    }

    int max(Fraction[] arr){
        // если нет дроби то -1 возращаем
        Fraction max = Fraction.valueOf(0);
        int index = -1;

        for(int i=0; i<arr.length; i++){
            if(max.compareTo(arr[i].fractionPart())<0){
                max=arr[i].fractionPart();
                index=i;
            }
        }
        mainRow = index;
        return index;
    }


    void restrictions(){
        Fraction[][] table = new Fraction[this.table.length+1][this.table[0].length];
        String [] base = new String[this.base.length+1];
        for(int i=0; i<this.table.length-1; i++){
            for(int j=0; j< table[0].length;j++){
                table[i][j] = this.table[i][j];

            }
            base[i]=this.base[i];
        }



        addW(table);
        base[base.length-1] = "w1";
        // вести отчет ограничений и приписывать цифру нужную
        for(int i=0; i< table[0].length;i++){
            table[table.length-1][i] = this.table[table.length-2][i];
        }

        this.base = base;
        this.table = table;
        negative(table.length-2);
        this.setAll(this.table);

    }

    void addW(Fraction[][] table){
        Fraction zero = new Fraction(0, 1);
        for(int i=0; i < table[0].length;i++){
            if(this.table[mainRow][i].compareTo(zero) > 0 ){
                table[table.length-2][i] =
                        this.table[mainRow][i].fractionPart();
                // выделяем дробную часть
            } else {
                table[table.length-2][i]= Fraction.valueOf((this.table[mainRow][i].ceil()-1)*-1)
                        .add(this.table[mainRow][i]);
            }
        }
    }

}

