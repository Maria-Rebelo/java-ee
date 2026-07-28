package com.exercicio;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App 
{
    public static void main( String[] args )
    {
        
        ObjectMapper mapper = new ObjectMapper();
        
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

            
            try {
                HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                
                JsonNode jsonNode = mapper.readTree(response.body());
                
                String status = jsonNode.get("status").asText();
                
                if (status.equals("Alive")) {
                    contVivos++;

                } else if (status.equals("Dead")) {
                    contMortos++;
                    
                    String species = jsonNode.get("species").asText();

                    if (species.equals("Alien")) {
                        System.out.println("Um Alien foi encontrado morto com o ID "+i+"!");
                        
                        JsonNode arrayEpisode = jsonNode.get("episode");
                        String urlNovo= arrayEpisode.get(arrayEpisode.size()-1).asText();
                        
                        HttpRequest request2=HttpRequest.newBuilder()
                        .uri(URI.create(urlNovo))
                        .GET()
                        .build();
                        
                        HttpResponse<String> response2 = client.send(request2, HttpResponse.BodyHandlers.ofString());
                        JsonNode jsonNode2 = mapper.readTree(response.body());
                
                        String nameEp = jsonNode.get("name").asText();
                        System.out.println( "[ALERTA FORENSE] O último registo do alien morto foi no episódio: "+nameEp);
                    } 
                } else {
                    contDesc++;
                }
            
            } catch (Exception e) {
                System.out.println( "Erro a ler String json" );
            }
           
            
        }
        System.out.println("Detetados "+contVivos+" personagens VIVOS e "+contMortos+" personagens MORTOS e "+contDesc+" com paredeiro desconhecido nos primeiros 20 registos.");
    }
}
