import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ApiTest {
    public static void main (String[] args) throws Exception {
        HttpClient client=HttpClient.newHttpClient();
        String urlBase="https://rickandmortyapi.com/api/character/";
        String urlPedido;
        int contVivos=0;
        int contMortos=0;
        int contDesc=0;
        for(int i = 1; i <= 20; i++){
            urlPedido=urlBase + i;
            HttpRequest request=HttpRequest.newBuilder()
            .uri(URI.create(urlPedido))
            .GET()
            .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
           
            String[] resposta = response.body().split(",");
            System.out.println(resposta[1]);
            if (resposta[2].contains("Alive")) {
                contVivos++;
            } else if (resposta[2].contains("Dead")) {
                contMortos++;
                if (resposta[3].contains("Alien")) {
                    System.out.println("Um Alien foi encontrado morto com o ID "+i+"!");
                } 
            } else {
                contDesc++;
            }
            
        }
        System.out.println("Detetados "+contVivos+" personagens VIVOS e "+contMortos+" personagens MORTOS e "+contDesc+" com paredeiro desconhecido nos primeiros 20 registos.");
    }
}
