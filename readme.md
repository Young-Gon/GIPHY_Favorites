# ![icon](./image/Giphy.PNG) GIPHY Favorites
[GIPHY API](https://developers.giphy.com/docs/api/) 를 사용하여 Trending GIF목록이 나오는 
Trending GIFs 화면과 이 GIF에 **"좋아요"** 표시를 할수 있는 Favorites 화면을 제작

## 요구사항
1. Trending GIFs 목록 화면   
목록 화면에서 각 아이템에 Favorite 버튼이 노출됨.
Favorite 버튼은 해당 아이템의 Favorite 상태를 나타내며, 누르면 Toggle 됨.
2. Favorites 화면   
Favorite 된 아이템들의 목록
3. Favorites된 목록은 앱을 다시 시작해도 유지되도록 구현
4. Target API 29 / Min API 19

### 빌드 요구사항
1. Android Studio 4.0 이상
2. SDK 29 이상
3. NDK 필요

### 요구사항 분석
#### 1. 서버 분석
1. [GIPHY API](https://developers.giphy.com/docs/api/) 서버는 GIF 목록을 나타내고 페이징 기능이 있어 **paging 기능**이 필요
2. GIF 목록의 아이템은 수시로 추가되기 때문에 **refresh 기능**이 필요
3. 네트워크 케싱을 위해 **DB** 필요
#### 2. UI 분석
1. Trending GIFs 목록 화면과 Favorites 화면 **두개의 화면** 필요
2. Favorites 리스트 저장을 위해 **DB** 필요
3. GIF 재생을 위해 GIF 재생을 지원하는 **Image Load Library** 필요
#### 3. 앱 분석
1. GIF를 네트워크로 부터 불러오면서도 화면 사용성을 해치지 않고 효율적으로 화면을 구성하는지가 관건 
2. Min API 19를 위해 SDK API 레벨에 신경 쓸것

### 앱 구조
전체적으로 MVVM 페턴을 따르고 있습니다  
네트워크로 부터 들어온 GIF 리스트는 DB에 저장이 되고 이 정보는 ViewModel로 들어 옵니다
이렇게 들어온 데이터는
[Android Databinding](https://developer.android.com/topic/libraries/data-binding?hl=ko) 과
[LiveData](https://developer.android.com/topic/libraries/architecture/livedata?hl=ko) 를
사용해 layout과 연결 됩니다  
이때 사용하는 데이터베이스 모듈과 네트워크 모듈은 [Hilt](https://developer.android.com/training/dependency-injection/hilt-android)
DI 라이브러리로 관리 합니다

#### 화면  페키지 구성
[MainActivity](./app/src/main/java/com/gondev/giphyfavorites/ui/main/MainActivity.kt), [GalleyActivity](./app/src/main/java/com/gondev/giphyfavorites/ui/gallery/GalleryActivity.kt)
두개의 화면으로 구성 되어 있습니다
1. [GalleyActivity](./app/src/main/java/com/gondev/giphyfavorites/ui/gallery/GalleryActivity.kt): 좌우로 한장씩 넘겨 가며 GIF의 오리지널 이미지를 볼 수 있는 화면입니다.   
추가적으로 제작한 거라 요구 사항에 포함 되지 않습니다
2. [MainActivity](./app/src/main/java/com/gondev/giphyfavorites/ui/main/MainActivity.kt): Trending GIFs 목록 화면과 Favorites 화면을 하위 항목으로 가지고 있으며 TabView로 표시하고 있습니다  
   1. [TrendingFragment](./app/src/main/java/com/gondev/giphyfavorites/ui/main/fragments/trending/TrendingFragment.kt): Trending GIFs 목록을 표시 합니다
   2. [FavoriteFragment](./app/src/main/java/com/gondev/giphyfavorites/ui/main/fragments/trending/TrendingFragment.kt): "좋아요" 표시한 GIFs 목록을 표시 합니다
   
#### 모델  페키지 구성 
1. [network](./app/src/main/java/com/gondev/giphyfavorites/model/network)
   1. [api](./app/src/main/java/com/gondev/giphyfavorites/model/network/api): GIPHI API와 통신하는 서비스를 제공합니다 
   2. [response](./app/src/main/java/com/gondev/giphyfavorites/model/network/response): 서버로 부터 받은 데이터를 담습니다 
2. [database](./app/src/main/java/com/gondev/giphyfavorites/model/database)
   1. [entity](./app/src/main/java/com/gondev/giphyfavorites/model/database/entity): 디비 스키마를 구성합니다 
   2. [dao](./app/src/main/java/com/gondev/giphyfavorites/model/database/dao): entity를 쿼리 합니다 
3. di.module: 위 두개의 페키지를 모듈화 하여 ViewModel에 전달 하기 위한 di 페키지 

### 오픈소스 라이브러리
1. [Android Architecture components](https://developer.android.com/jetpack/androidx/releases/lifecycle)  
안드로이드 ViewModel을 지원하고 라이프사이클에 맞게 제어 하는 역할을 합니다
2. [Dagger-Hilt](https://developer.android.com/training/dependency-injection/hilt-android)  
의존성주입(Dependency Injection) 라이브러리입니다   
클레스에서 사용하는 인스턴스를 외부에서 주입 받아
클레스간 의존성을 줄이고 테스트를 용의하게 해줍니다 Dagger는 컴파일 단계에서 의존성을 주입해 주는 강력한
라이브러리이지만 사용법이 간단하지 않아 제대로 사용 하려면 많은 학습이 필요합니다
hilt는 dagger의 많은 부분을 ViewModel에 맞게 재조정하여 쉽고 간편하게 사용할 수 있는 장점이 있습니다
따라서 이번 프로젝트에서는 hilt를 사용하여 의존성 주입을 해보겠습니다
    > 참고: Hilt와 데이터 바인딩을 모두 사용하는 프로젝트에는 Android 스튜디오 4.0 이상이 필요합니다.
3. [Retrofit](https://square.github.io/retrofit/)   
httpClient rapper library입니다   
http 프로토콜을 통해서 WAS에 접근하는 라이브러리입니다 Rest API 메소드를 제공할 뿐만 아니라
Gson과 연계해서 파싱지원을 하는 등 강력하고 유연한 API 콜을 제공합니다
     > 참고: 최신버전 2.9.0부터 Android API 21을 요구하므로
     2.8.1을 사용 하였습니다
4. [Room](https://developer.android.com/topic/libraries/architecture/room)   
SQLite 기반의 데이터 영속화 라이브러리입니다   
네트워크 데이터를 DB에 저장하고 케쉬 처럼 읽고 쓸 수 있습니다
네트워크 통신전에 DB에 데이터가 있으면, 이 데이터로 화면을 구성하고 네트워크 데이터로 수정합니다
이렇게 하면 화면 반응성이 빨라집니다
5. [Paging](https://developer.android.com/topic/libraries/architecture/paging?hl=ko)   
페이징 라이브러리입니다   
trending gif 리스트를 분할하여 조금식 가저오기 위한 기능 입니다
이렇게 하면 사용자가 필요한 만큼만 보여 주기 때문에 성능이나 효율성 측면에서 많은 도움이 됩니다
6. [Timber](https://github.com/JakeWharton/timber)   
로그 라이브러리입니다
7. [Glide](https://bumptech.github.io/glide/)   
이미지 로드 라이브러리입니다
네트워크 이미지 다운로드, 이미지 케싱, 이미지 변환, 이미지 사용 메모리 관리, 화면에 이미지 출력을 위해 사용합니다
8. [Webpdecoder](https://github.com/zjupure/GlideWebpDecoder)  
glide가 webp를 재생할 수 있도록 하는 Glide 플러그인 입니다
    > 참고: NDK 리소스가 필요 합니다 