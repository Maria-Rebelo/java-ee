package servlet;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.Response;



@Path("/census")
public class App 
{
    @GET
    public Response doGet(@DefaultValue("1")@QueryParam("offset") String offset,@DefaultValue("20")@QueryParam("limit") String limit, @DefaultValue("true")@QueryParam("showAlert") String showAlert) 
    {
        Response.ResponseBuilder responseBuilder = Response.ok();
        

        ObjectMapper mapper = new ObjectMapper();
        
        HttpClient client=HttpClient.newHttpClient();
    
        String urlBase="https://rickandmortyapi.com/api/character/";
        String urlPedido;
        int contVivos=0;
        int contMortos=0;
        int contDesc=0;
        int x=0;
        int y=0;
        Boolean alert=null;
        Boolean erroSintaxe = false;
        String msg="";
        String respostaString ="--------------------\n";

       
        if (isNumeric(offset)){
             if (Integer.parseInt(limit)>0 && Integer.parseInt(limit)<=50) {
                x=Integer.parseInt(offset);
             } else {
                erroSintaxe=true;
                msg="O parametro offset tem de ser um numero inteiro entre 1 e 50";
            }
        }
        else{
            erroSintaxe=true;
            msg="O parametro offset tem de ser um numero inteiro";
        }
        
        if (isNumeric(limit)){
            if (Integer.parseInt(limit)>0 && Integer.parseInt(limit)<=50) {
                y=Integer.parseInt(limit);                    
            } else {
                erroSintaxe=true;
                msg="O parametro limit tem de ser um numero inteiro entre 1 e 50";
            }
        }
        else{
            erroSintaxe=true;
            msg="O parametro limit tem de ser um numero inteiro";
        }
        
        
        if (showAlert.contentEquals("true") || showAlert.contentEquals("false")){
            alert=Boolean.parseBoolean(showAlert);
        }
        else{
            erroSintaxe=true;
            msg="O parametro showAlert tem de ser \"false\" ou \"true\"";
        }
          
       
        if (!erroSintaxe)
        {
            for(int i = x; i < x+y; i++){
                urlPedido=urlBase + i;
                HttpRequest request=HttpRequest.newBuilder()
                .uri(URI.create(urlPedido))
                .GET()
                .build();
        
                
                try {
                    HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
                    //Thread.sleep(1000);
 
                    JsonNode jsonNode = mapper.readTree(response.body());
                    
                    String status = jsonNode.get("status").asText();
                    
                    
                    if (status.equals("Alive")) {
                        contVivos++;
        
                    } else if (status.equals("Dead")) {
                        contMortos++;
                        
                        String species = jsonNode.get("species").asText();
        
                        if (species.equals("Alien") && alert) {
                            respostaString += "Um Alien foi encontrado morto com o ID "+i+"!\n" + //
                            "--------------------\n";

                            JsonNode arrayEpisode = jsonNode.get("episode");
                            String urlNovo= arrayEpisode.get(arrayEpisode.size()-1).asText();
                            
                            request=HttpRequest.newBuilder()
                            .uri(URI.create(urlNovo))
                            .GET()
                            .build();
                            
                            response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            jsonNode = mapper.readTree(response.body());
                    
                            String nameEp = jsonNode.get("name").asText();
                            respostaString +="[ALERTA FORENSE] O ultimo registo do alien morto foi no episodio: "+nameEp+"\n" +//
                            "--------------------\n";
                        } 
                    } else {
                        contDesc++;
                    }
                
                } catch (Exception e) {
                    System.out.println( "Erro a ler String json"+i);
                    //i--;
                }
            
                
            }
            
            respostaString +="Detetados "+contVivos+" personagens VIVOS e "+contMortos+" personagens MORTOS e "+contDesc+" com paredeiro desconhecido.\n" + //
           "--------------------\n";
            
            responseBuilder.entity(respostaString);
        }
        else
        {
            respostaString += " \"status\": 400,\n" + //
                                "  \"error\": \"Bad Request\",\n" + //
                                "  \"message\": \""+msg+"\n" + //
                                "--------------------\n";
            
            responseBuilder.status(400);
            responseBuilder.entity(respostaString);
            
            
        }
        Response resposta = responseBuilder.build();
        return resposta;
    }


    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    
    }
        
}

