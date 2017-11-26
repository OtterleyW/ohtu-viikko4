package ohtu.verkkokauppa;
import org.junit.Before;
import org.junit.Test;
import static org.mockito.Mockito.*;


public class VerkkokauppaTest {
    
    Pankki pankki;
    Viitegeneraattori viite;
    Varasto varasto;
    Kauppa k;
    
    @Before
    public void setUp(){
        // luodaan ensin mock-oliot
        pankki = mock(Pankki.class);

        viite = mock(Viitegeneraattori.class);
        // määritellään että viitegeneraattori palauttaa viitten 42
        when(viite.uusi()).thenReturn(42);

        varasto = mock(Varasto.class);
        // määritellään että tuote numero 1 on maito jonka hinta on 5 ja saldo 10
        when(varasto.saldo(1)).thenReturn(10);
        when(varasto.haeTuote(1)).thenReturn(new Tuote(1, "maito", 5));
        
        when(varasto.saldo(2)).thenReturn(5);
        when(varasto.haeTuote(2)).thenReturn(new Tuote(2, "suklaa", 7));
        
        when(varasto.saldo(3)).thenReturn(-1);
        when(varasto.haeTuote(3)).thenReturn(new Tuote(3, "olut", 10));

        // sitten testattava kauppa 
        k = new Kauppa(varasto, pankki, viite);
        
    }
    
    @Test
    public void ostoksenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));
 
    }
    
    @Test
    public void kahdenOstoksenJalkeenOstostenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
     
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(2);      // ostetaan tuote 2 eli suklaata
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(12));

    }
    
    @Test
    public void kahdenSamanOstoksenJalkeenOstostenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {
    
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(1);      // ostetaan tuotetta numero 1 uudestaan
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(10));

    }

    @Test
    public void ostaOstosJollaOnSaldoaJaOstosJollaEiOleSaldoaJonkaJalkeenOstostenPaaytyttyaPankinMetodiaTilisiirtoKutsutaan() {

        k.aloitaAsiointi();
        k.lisaaKoriin(1);     // ostetaan tuotetta numero 1 eli maitoa
        k.lisaaKoriin(3);      // ostetaan tuotetta numero 1 uudestaan
        k.tilimaksu("pekka", "12345");

        // sitten suoritetaan varmistus, että pankin metodia tilisiirto on kutsuttu
        verify(pankki).tilisiirto(eq("pekka"), anyInt(), eq("12345"), anyString(), eq(5));

    }
    
    @Test 
    public void aloitaAsiontiNollaaEdellisenOstoksen(){
        
        k.aloitaAsiointi();
        k.lisaaKoriin(1);     
        
        k.aloitaAsiointi();
        k.lisaaKoriin(2);     
        k.tilimaksu("pekka", "12345");
        
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(7));
        
    }
    
    @Test
    public void kauppaPyytaaUudenViitenumeronJokaMaksutapahtumalle(){
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.tilimaksu("pekka", "12345");
                
       verify(viite, times(1)).uusi();
       
        k.aloitaAsiointi();
        k.lisaaKoriin(2);
        k.tilimaksu("liisa", "54321");
        verify(viite, times(2)).uusi();
    }
    
    @Test
    public void poistaKoristaPoistaaTuotteen(){
        k.aloitaAsiointi();
        k.lisaaKoriin(1);
        k.lisaaKoriin(2);
        k.poistaKorista(1);
        k.tilimaksu("liisa", "54321");
        
        verify(pankki).tilisiirto(anyString(), anyInt(), anyString(), anyString(), eq(7));
    }
    
    
    
   
}
