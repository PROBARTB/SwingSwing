Robimy w java swing grę.

package vechicle.

RailVechicle - posiada wektor prędkości, kierunku (pierwszego wózka), położenie (pierwszego wózka), zawiera człony i wózki, przegub

Joint - zawiera odstęp między członami i max kąt zgięcia

Tram - typ pojazdu, zawiera rzeczy typowe dla tramwajów ale nie dla pociągów (na razie nie dawaj ich!)

Bogie - wózek pojazdu, posiada pozycję w metrach na torze (pozycja środka wózka), wektor kierunku.

Section - człon pojazdu, ma długość [m], wózki na określonej pozycji względem początku [m], wektor pozycji xz w świecie, wektor kierunku.

package vechicles. trams.

PesaSwing - typ tramwaju, ma zdefiniowane przegub i człony: [1. 4m z wózkiem na 3m, 2. 5m bez wózków, 3.  2m z wózkiem na 1m, 4. 5m bez wózków, 5. 4m z wózkiem na 1m].

package tracks.

TrackSegment - segment toru, posiada długość i getter, nextSegment, wektor kierunku końcowego punktu, metodę zwracającą wektor kierunku dla danego punktu segmentu,

CurvedSegment - typ segmentu, ma dwa konstruktory: 1. offsetX, offsetZ względem punktu początkowego, różnica kątów punktów początkowego i końcowego, 2. promień łuku, długość łuku - z tego są wyliczane wartości dla konstruktora 1. Z tego jest liczony ładny łuk używając krzywej beziera.

StraightSegment - prosty odcinek toru o danej długości.

SwitchSegment - typ segmentu, zawiera segment curved i segment straight i pozwala wybrać ścieżkę jako jeden z tych segmentów.

package rendering.

TracksRenderer - odpowiada za renderowanie torów. Tory to dwie równoległe ścieżki w odstępie Z 1,435m.

VechicleRenderer - odpowiada za renderowanie pojazdu i jego zachowanie na torze.

RenderingEngine - ogólny silnik renderowania pseudo-3d. Wszystkie pozycje (pojazdów, torów) są w układzie X Z, a ekran jest w X Y, dlatego potrzebujemy tego komponentu. Im coś ma większe Z względem kamery tym jest zeskalowane na mniejsze i jest lekko wyżej (y ekranu) - perspektywa. Lokalny (kamery) wektor kierunku powoduje odpowiednie transformacje elementu (robienie z rectangle trapezu osiągając efekt obrotu 3d). To musi działać też dla pathów, jakimi są tory - pathy szyn są w odstępie Z 1,435m czyli dalsza szyna powinna być lekko mniejsza i wyżej na ekranie. To przesunięcie y (perspektywa) niech będzie stałą możliwą do zmiany. Pathy muszą być konwertowane tak, że jeśli np. tor ma zakręt i biegnie pod kątem 90st, tj. prosto na kamerę to im bliżej path jest kamery tym jest szersze i odstęp pomiędzy szynami jest większy (idą one lekko na zewnątrz).

package world.

ScenePanel - panel gry dziedziczący po JPanel, w nim renderują się wszystkie rzeczy i obsługiwane są keyeventy sterowania prędkością pojazdu w/s (na razie pierwszego pojazdu z listy pojazdów w world).

World - zawiera tory, pojazdy, w przyszłości inne obiekty

Game - posiada metodę main gdzie zdefiniowany jest świat z jednym pojazdem i kilkoma odcinkami torów (proste, łuki, rozjazdy)

Camera - można ją podczepić do jakiegokolwiek członu pojazdu, wtedy lokalny układ współrzędnych ma środek tam gdzie środek tego członu, to samo z kierunkiem obrotu. RenderingEngine korzysta z tych lokalnych układów do renderowania.

package helpers.

Units - przeliczniki jednostek, zawiera przelicznik metra na piksele używane w rendererze.

Vec2 - wektor używany w wielu miejscach.

Opis działania segmentów torów:World posiada graph segmentów (ścieżki rozdzielają się po rozjazdach) i kolejne segmenty są tworzone na końcach poprzednich, aby razem tworzyć jeden ciągły tor. Pojazd musi płynnie jechać po takim torze utworzonym z segmentów.

Opis działania członów, wózków, przegubu:Wózki swoim środkiem bezwzględnie są przyczepione do toru i za nim podążają, obracają się odpowiednio na łuku (do tego służy metoda zwracającą wektor kierunku dla danego punktu segmentu). Pozycja na torze jest liczona w metrach. Człony rozciągają się pomiędzy mocowaniami: na środku każdego wózka oraz na końcach członów, jeśli na danym końcu jest joint z innym członem. Joint ma max kąt zgięcia i swoją długość, czyli odstęp pomiędzy członami.

oś Z oznacza głębokość względem kamery, kamera dziedziczy obrót po członie do którego jest przyczepiona, ale to ma tylko znaczenie przy renderowaniu, nie w logice!!
stosujemy takie transformacje, aby symulować 3d, czyli np. skalować obrazek (człon) tak aby jedna krawędź była dłuższa od drugiej a górna i dolna krawędź po skosie (trapez) kiedy człon jest obrócony względem kamery. przesunięcie Y (perspektywa) ma być mnożnikiem i od niej też ma zależeć skaowanie obiektów (im głębiej tym mniejsze).
Łuk jako krywa beziera ma być zrobiony tak, jak wyjdzzie najprościej. Wózki mają używać metod segmentu aby określić pozycję i obrót na podstawie pozycji w metrach na torze, ma to być na tyle dokładne aby dobrze wyglądało, ale było jak najprostsze.
Przegub ogranicza różnicę kierunków członów. niech na razie przekroczenie kąta ogranicza dalszy obrót.
Kamera przyczepiona do członu obraca się razem z nim, ten człon jest nieprzeskalowany w centrum ekranu, a reszta jest renerowana na podstawie lokalnego układu wzpółrzędnych względem kamery. Nie potrzebujemy zoomu.
W świecie odstep między szynami ma być stały, dopiero rendering go zmienia. Możemy rysować pathy po segmencie. Wszystko ma działać na podobnej zasadzie - w logice używamy układu współrzędnych xz, a dopiero renderingEngine skaluje, rysuje. renderer torów i pojazdów używa też układu współrzędnych xz i dopiero używa renderingengine który tworzy z tego xy ekranu.
Logika ma być trzemana w world, a scenepanel to tylko kontener gdzie renderujemy na podstawie world. Pętla gry na razie może być oparta na swing Timer.

Napisz pełny kod, nie szkielety. Kod ma być dobry, spełniać założenia projektu, uwzględniać powyższe odpowiedzi na pytania doprecyzowujące. Zastanów się głębiej podczas pisania kodu, ponieważ to duży skomplikowany projekt na którym mi bardzo zależy żeby był dobry.