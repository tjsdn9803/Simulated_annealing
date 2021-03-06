# 모의 담금질 기법을 이용한 curvefitting

_______________

## 0) 모의 담금질 기법이란 

금속 공학의 담금질 기법에서 유래한 알고리즘으로 초기에 온도가 높을 때는 이웃해를 자유롭게 탐색하다가 온도가 낮아짐에 따라 점점 더 규칙적인 방식으로 찾게된다.

## 1) 3~4차 함수의 전역 최적점을 찾을 수 있는 모의담금질 기법

______________

1~2차 함수에서 전역 최적점은 지역 최적점을 계속 찾아 나가다 보면 나오기 떄문에 모의 담금질 알고리즘을 이용하여 최적점을 찾을 때 항상 현재 값보다 더 나은 값을 찾도록  설계한다면 결국 전역 최저점을 찾게 될것이다.   

하지만 같은 방법으로 전역 최적해를 탐색하게 되면 지역 최적해에서 빠져나오지 못하는 경우가 생긴다. 이러한 상황을 막기 위해 이웃해의 찾는 방식과 더 안좋은 해를 찾게 되더라도 확률에 따라 그 점을 이웃해로 설정하게하는 확률을 설정해주어야한다.

### 1-1) 이웃해의 설정

--------------------

이웃해를 찾을 떄는 온도가 낮아짐에 따라 활동 범위가 줄어들어야한다.

첮번째는 탐색횟수를 온도 만큼 설정하는 것이다. 탐색횟수를 온도 만큼 설정하게 되면 온도가 낮아짐에 따라 이웃해의 탐색 횟수가 적어지므로 점점 근삿값에 다다르게 된다.

```java
int kt = (int) t;
            for(int j=0; j<kt; j++) {
                ...
              	...
                ...
            }
```

다른 방법으로는 이웃해를 탐색 할 범위를 온도가 낮아짐에 따라 줄여주는 것이다.

```java
for(int j=0; j<kt; j++) {
                double a1 = (r.nextDouble() * (a_upper - a_lower) + a_lower)*Math.exp(-100/t) + a0;
```



### 1-2) 확률 설정

------------------

확률에는 두가지 변수가 있는데 p.fit의 차이인 d와 온도 t가 있다.

d의 값이 클수록 현재의 해와는 멀리 떨어진 값이라는 뜻이므로 확률은 줄어들어야 한다.

그리고 t가 작아 질수록 이웃해의 움직임이 줄어들어야 하므로 확률은 줄어들어야 한다. 

위의 목적을 위해 확률을 나타내면 e^(-d/t) 꼴이 된다.

```
double d = Math.sqrt(Math.abs(f1 - f0));
double p0 = Math.exp(-d/t);
if(r.nextDouble() < p0) {
                        a0 = a1;
                        b0 = b1;
                        f0 = f1;
                        hist.add(a0);
                    }
```

d는 상대적인 숫자이므로 실제 데이터에서는 계수를 조정해 주어야 한다.



잘 작동하는지 확인하기위해 초기온도 100도 온도 감쇠비율 0.99 최대 최솟값 -3,+3확률 p0 = e^(-d/t) 으로 두고 

임의의 4차함수 8a^4 + 3a^3 -6a^2 로 실험을 해본 결과

![제목 없음](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%A0%9C%EB%AA%A9%20%EC%97%86%EC%9D%8C.png)

결과가 잘 잡히지 않았다 그래서 확률 값을 출력해본 결과 온도가 60도 까지 떨어져도 확률은 0.9내외로 유지되고 있어 값이 거의 랜덤하게 나오고 있었다.

확률 p0의 계수를 조절하기위해  d에 120을 곱해준 결과 온도가 낮아지면서 확률이 줄어들어서 오차 범위 0.5내외로 근사한 결과를 얻어낼 수 있었다.

![4차 결과](https://github.com/tjsdn9803/Simulated_annealing/blob/main/4%EC%B0%A8%20%EA%B2%B0%EA%B3%BC.png)

## 2) 임의의 데이터의 curve fitting을 위한 parameter estimation

----------------

### 2-1)데이터 소개

내가 다루어볼 데이터는 월별 나스닥 지수이다.

[https://kr.investing.com/](https://kr.investing.com/)

에서 나스닥 지수 데이터를 다운받아 엑셀로 열어서 분산형 차트로 표현한 결과 (총138개의 데이터)

![나스닥 엑셀 그래프 a](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EB%82%98%EC%8A%A4%EB%8B%A5%20%EC%97%91%EC%85%80%20%EA%B7%B8%EB%9E%98%ED%94%84%20a.PNG)

지수함수의 형태로 나타났다.(편의를 위해 x축은 한달이 지날때마다 1이 증가하게 설정하였다.)

이를 a*e^(bx)의 형태라 생각하고 a와 b를 찾기위해 모의 담금질 기법을 사용하겠다.

cost function은 a와 b에 어떤 값을 대입하여 실제데이터와 비교해 보았을 때  차이의 합으로 정의 할 때 

cost function의 값이 가장 작을 때의 a*e^(bx)가 실제 데이터와 가장 근사한 그래프 일 것이다.

### 2-2) 모의 담금질 기법을 이용한 parameter estimation

cost function의 최솟값을 찾기위해 모의담금질 기법을 이용하기 위해 fit메소드를 수정한다.

```java
public double fit(double a,double b) {
                double avg=0;
                double sum=0;
                double value=0;
                for(int i=0;i<138;i++){
                    value = Data[i]-(a*Math.exp(b*i));
                    if(value<0){
                        sum += value * (-1);
                    }
                    else{
                        sum += value;
                    }
                }
                avg = sum/138;
                return avg;
            }
```

그리고 y축과 x축의 범위를 설정해주고 확률 p0를 조절하여 작동시키면

![실행결과4](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%8B%A4%ED%96%89%EA%B2%B0%EA%B3%BC4.PNG)

적절한 a와 b값이 나온다.

## 3)성능분석 및 결과

--------------

위에서 다룬 curve fitting은 데이터의 갯수가 매우 많아 cost function의 차이가 매우 크고 범위가 넓었다. 그래서 확률의 값을 조절하기가 어려웠다(실제로 확률의 값이 매우 작아 거의 0이었고 차이d가 어느정도 작아야 확률이 그나마 커졌다). 하지만 그래프가 멀어질수록 cost function이 커지고 가까워질수록 커지는 형태여서 아래로 둥근 2차함수그래프를 모의 담금질 기법으로 찾아내는 형태여서 지역 최적점에 빠질 경우가 없어서 전역최적점을 찾기에 까다롭지 않았다. 

하지만 만약 cost function의 그래프가 4차이상의 복잡한 그래프로 나온다면 계수를 잘 조절해 주지 않는 한 전역 최적점을 찾기가 매우 까다로울것 같다. 

### 3-1) 확률p0에 따른 이웃해의 탐색활동        

위에서 p0을 조절하여 적절한 값을 갖게 되는 과정을 설명 하겠다.            
                 
![d 10](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%83%88%20%ED%8F%B4%EB%8D%94/d%2010.PNG)     
      
d에 10을 곱한후 관찰한 이웃해의 x축 변화이다. 상당히 폭이 큰것을 볼수 있다.             
![d 80](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%83%88%20%ED%8F%B4%EB%8D%94/d%2080.PNG)      
         
![d 200](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%83%88%20%ED%8F%B4%EB%8D%94/d%20200.PNG)      
         
![d 600](https://github.com/tjsdn9803/Simulated_annealing/blob/main/%EC%83%88%20%ED%8F%B4%EB%8D%94/d%20600.PNG)       
         
d의 계수가 커짐에 따라 p0가 작아지면서 x축의 변화량이 줄어든다. 이는 더 근삿값의 해를 가질 수 있다는 장점이 있지만 자칫 잘못하면 지역 변수에서 빠져 나오지 못한다는 단점을 가진다.



