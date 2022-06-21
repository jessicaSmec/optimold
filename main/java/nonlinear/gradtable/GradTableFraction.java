package nonlinear.gradtable;

import forAll.Fraction;
import gradient.*;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static java.math.BigDecimal.ROUND_HALF_UP;
import static java.math.BigInteger.TWO;

public class GradTableFraction {


    private FunFraction f;
    private DfFraction[] dx;
    private Fraction[]x;

    private Fraction[]nextX;

    private Map<String,Fraction> params= new HashMap<>();
    private Map<String,Fraction[]> arrParams = new HashMap<>();

    // 1 констрjктуры
    //sgd


    public GradTableFraction(){}

    public GradTableFraction(FunFraction f,  Fraction[]x,DfFraction[] dx, Map<String,Fraction> params, Map<String,Fraction[]> arrParams ){

        setAll(f, x, dx, params, arrParams );
    }
/*
    private void setAll(FunFraction f,  Fraction[]x,DfFraction[] dx, Map<String,Fraction> params, Map<String,Fraction[]> arrParams){
        this.f=f;
        //this.x=x;
        this.x=new Fraction[x.length];
        System.arraycopy(x, 0, this.x, 0, x.length);
        this.dx=dx;
        this.params = params;
        this.arrParams = arrParams;

    }

 */


    private void setAll(FunFraction f, Fraction[]x, DfFraction[] dx, Map<String,Fraction> params, Map<String,Fraction[]> arrParams){
        this.f=f;
        //this.x=x;
        this.x=new Fraction[x.length];
        //System.arraycopy(x, 0, this.x, 0, x.length);
        this.params = params;
        this.arrParams = arrParams;
        if (dx != null) {
            this.dx = dx; // тоже копировать?
        } else {
            dx = new DfFraction[x.length];
            for(int i=0; i < x.length; i++){
                int finalI = i;
                dx[i] = (w)-> df(finalI);
            }
        }

        nextX = new Fraction[x.length];
        this.x = new Fraction[x.length];


        for(int l=0; l< x.length; l++){
           // String value = String.valueOf(x[l]);
            nextX[l] = new Fraction(x[l].getNumenator(), x[l].getDenominator());
            this.x[l] = new Fraction(x[l].getNumenator(), x[l].getDenominator());
        }
    }



    public Fraction df(int i){
        Fraction eps = Fraction.valueOf(0.0001); // отдельно инициализировать?
        Fraction[] _x = new Fraction[x.length];
        System.arraycopy(x, 0, _x, 0, x.length);
        _x[i]= _x[i].add(eps);
        //return ((f.f(_x).subtract(f.f(x))).divide(eps, 5, RoundingMode.CEILING));
        Fraction f1 = f.f(_x);
        Fraction f2 = f.f(x);
        Fraction ff = f1.subtract(f2);
        Fraction res = ff.divide(eps, 8 , RoundingMode.CEILING);
        return res;

    }


/*
    public Fraction[] sgd() {


        int iter = (int) params.get("iter").ceil();

        boolean check =false;
        // int iter =10;
        Fraction lr = params.get("lr");
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;
        Fraction[] nextX = new Fraction[this.x.length];
        // nextX[0] = x[0];
        System.arraycopy(this.x, 0, nextX, 0, x.length);
        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < x.length; j++) {

                //if(stop!=null && stop)
                // BigDecimal diffrent=
                //Fraction a= this.df(j);
                Fraction a= this.dx[j].df(this.x);
                nextX[j] = nextX[j].subtract(lr.multiply(a));
                //nextX[j] = this.x[j];
            }
            if(stop !=null) {
                for (int k = 0; i < x.length; i++) {
                    //if (nextX[k].subtract(x[k]).abs().compareTo(stop)!=-1){
                    //  check=false;
                    //}

                    check= nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;

                }
            }

            if(check) break;

            System.arraycopy(nextX, 0, this.x, 0, x.length);
        }

        return this.x;
    }
*/





    public Fraction[] sgd() {

        int iter = (int) params.get("iter").getNumenator();
        boolean check =false;
        int length = x.length;
        Fraction lr = params.get("lr");
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;


        // System.arraycopy(this.x, 0, nextX, 0, x.length);


        for (int i = 0; i < iter; i++) {


            for (int j = 0; j < length; j++) {

                System.out.println("-----");
                System.out.println(i);
                System.out.println(j);
                //nextX[j] = nextX[j].subtract(lr.multiply(df(j)));
                nextX[j] = nextX[j].subtract(lr.multiply(dx[j].df(x)));
            }
            if(stop !=null) {
                for (int k = 0; i < length; i++) {
                    check= nextX[k].subtract(x[k]).abs().compareTo(stop) == -1;
                }
            }

            if(check) break;

            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }

            //System.arraycopy(nextX, 0, this.x, 0, x.length);
            // под
        }

        return this.x;
    }




    public Fraction[] momentum() {
        //v=0 start mu=0.9 [0,5, 0,9, 0,95, 0,99].
        //Типичная настройка - начать с импульса около 0,5 и отжечь его до 0,99 или около того в течение нескольких эпох.

        Fraction lr = params.get("lr");
        int iter = (int) params.get("iter").ceil();
        Fraction mu = params.get("mu");
        Fraction v = params.get("v");
        Fraction[] nextX = new Fraction[this.x.length];
        System.arraycopy(x, 0, nextX, 0, x.length);


        for (int i = 0; i < iter; i++) {

            for (int j = 0; j < x.length; j++) {
                // x[j] -= x[j]*mu - lr * dx[j].df(x[j]);
                //nextX[j]=this.x[j];
                System.out.println("-----");
                System.out.println(i);
                System.out.println(j);

                Fraction for_mu=dx[j].df(x);
                Fraction for_v = lr.multiply(for_mu);
                v = v.multiply(mu).subtract(for_v);

                //v = mu * v - lr * dx[j].df(x[j],x);
                // nextX[j] += v;
                nextX[j] = nextX[j].add(v);

            }
            // x=nextX;
            System.arraycopy(nextX, 0, x, 0, x.length);
        }
        return x;
    }



    public static BigDecimal sqrt(BigDecimal value) {

        BigDecimal x = BigDecimal.valueOf(Math.sqrt(value.setScale(4, RoundingMode.HALF_UP).doubleValue()));
        return x.add(BigDecimal.valueOf(value.subtract(x.multiply(x)).doubleValue() / (x.doubleValue() * 2.0)));
    }


    public Fraction[] adam() {

//    config.setdefault("learning_rate", 1e-3)
        //          config.setdefault("beta1", 0.9)
        //        config.setdefault("beta2", 0.999)
        //      config.setdefault("epsilon", 1e-8)
        //    config.setdefault("m", np.zeros_like(w))
        //  config.setdefault("v", np.zeros_like(w))
        // config.setdefault("t", 0)

        int t = 0;
        Fraction[] m = this.arrParams.get("m");;
        Fraction[] v = this.arrParams.get("v");
        Fraction[] mt = new Fraction[x.length];
        Fraction[] vt = new Fraction[x.length];
        int iter = (int) params.get("iter").ceil();
        Fraction lr = params.get("lr");
        Fraction beta1 = params.get("beta1");
        Fraction beta2 = params.get("beta2");
        Fraction eps = params.get("eps");;
        Fraction nextX[] = new Fraction[this.x.length];

        System.arraycopy(x, 0, nextX, 0, x.length);

        Fraction one = Fraction.valueOf(1);
        for (int i = 0; i < iter; i++) {
            // в других уравнениях так же вынести
            Fraction[] dww = new Fraction[x.length];
            for (int k = 0; k < x.length; k++) {
                dww[k] = df(k);

            }

            for (int j = 0; j < x.length; j++) {

                System.out.println("-----");
                System.out.println(i);
                System.out.println(j);


                Fraction dw = dww[j];

                m[j] = beta1.multiply(m[j]).add(one.subtract(beta1).multiply(dw));
                //m[j] = beta1 * m[j] + (1 - beta1) * dw;
                // добавить и реализацию с учетом того что параметры реализуют
                // map передавать
                t++;

                v[j] = beta2.multiply(v[j]).add(one.subtract(beta2).multiply(dw.pow(2)));
                //v[j] = beta2 * v[j] + (1 - beta2) * dw * dw;
                //mt[j] = m[j] / (1 - Math.pow(beta1, t));
                //vt[j] = v[j] / (1 - Math.pow(beta2, t));
                mt[j] = m[j].divide(one.subtract(beta1.pow(t)), 6,  RoundingMode.CEILING);
                vt[j] = v[j].divide(one.subtract(beta2.pow(t)), 6, RoundingMode.CEILING);

                //nextX[j] -= mt[j] * lr / (Math.sqrt(vt[j]) + epsilon);
                Fraction sqrt = Fraction.valueOf(this.sqrt(vt[j].add(eps).bigDecimalValue()));
                nextX[j]= nextX[j].subtract(mt[j].multiply(lr).divide(sqrt, 4, RoundingMode.CEILING));



            }
            System.arraycopy(nextX, 0, x, 0, x.length);
        }


        return x;


    }




    public Fraction[] rmsprop() {


        int length = x.length;
        Fraction stop =  params.get("stop") != null ? params.get("stop") : null;
        boolean check =false;
        Fraction eps = params.get("eps");;
        Fraction lr = params.get("lr");
        int iter = (int) params.get("iter").getNumenator();
        //double mu = params.get("mu");
        //double v = params.get("v");
        Fraction decayRate = params.get("decayRate");


        //System.arraycopy(x, 0, nextX, 0, length);



        Fraction[] cache = new Fraction[length];


        Fraction zero = Fraction.valueOf(0);
        // for (int i = 0; i < cache.length; i++) {
        //cache[i] = zero; // посмотреть не сломается ли
        //  cache[i] = BigDecimal.valueOf(0);
        // }


        //cache[i] = zero; // посмотреть не сломается ли
        Arrays.fill(cache, BigDecimal.valueOf(0));


        Fraction one = Fraction.valueOf(1);
        for (int i = 0; i < iter; i++) {
            //BigDecimal[] dx_ = new BigDecimal[length];
            for (int k = 0; k < length; k++) {
                // dx_[k] = this.dx[k].df(this.x[k], this.x);
                //  dx_[k] = this.df(k); // лучше внутрь функции всунуть

            }
            for (int j = 0; j < length; j++) {
                cache[j] = decayRate.multiply(cache[j]).add((one.subtract(decayRate)).multiply(dx[j].df(x).pow(2)));
                //cache[j] = decay_rate * cache[j] + (1 - decay_rate) * dx_[j] * dx_[j];
                //
                //nextX[j] -= lr * dx_[j] / (Math.sqrt(cache[j]) + eps);
                // большие сомнения
                Fraction sqrt = Fraction.valueOf(Math.sqrt(cache[j].add(eps).bigDecimalValue().doubleValue()));
                //nextX[j]= nextX[j].subtract(lr.multiply(dx_[j])).divide(sqrt,RoundingMode.CEILING);
                //nextX[j] -= lr * dx_[j] / (Math.sqrt(cache[j]) + eps);
                nextX[j] = nextX[j].subtract( lr.multiply(dx[j].df(x)).divide(sqrt,10,RoundingMode.HALF_UP) );

                if(stop !=null) {
                    if ((j > 0 && check) || j == 0) {
                        check = nextX[j].subtract(x[j]).abs().compareTo(stop) == -1;
                    }
                }

                //  System.out.println(cache[j].doubleValue());
                // System.out.println(nextX[j].doubleValue());

                // nextX[j] = nextX[j].subtract(lr.multiply(dx_[j]).divide(BigDecimal.valueOf(Math.sqrt(cache[j].add(eps).doubleValue()))));
            }
            //System.arraycopy(nextX, 0, x, 0, x.length);



            if(check) break;


            for(int l=0; l<length; l++){
                x[l] = new Fraction(nextX[l].getNumenator(), nextX[l].getDenominator());
            }
        }
        //  cache = decay_rate * cache + (1 - decay_rate) * dx**2
        // x += - learning_rate * dx / (np.sqrt(cache) + eps)

        return x;

    }




}
