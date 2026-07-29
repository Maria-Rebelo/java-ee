package servlet;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet("/census")
public class App extends HttpServlet
{
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException 
    {
        ObjectMapper mapper = new ObjectMapper();
        
        HttpClient client=HttpClient.newHttpClient();
    
        String urlBase="https://rickandmortyapi.com/api/character/";
        String urlPedido;
        int contVivos=0;
        int contMortos=0;
        int contDesc=0;
        int x=1;
        int y=20;
        Boolean alert=true;
        String offset = req.getParameter("offset");
        String limit = req.getParameter("limit");
        String showAlert = req.getParameter("showAlert");
        Boolean erroSintaxe = false;
        String msg="";

        if (offset!=null)
        {
            if (isNumeric(offset)){
                x=Integer.parseInt(offset);
            }
            else{
                erroSintaxe=true;
                msg="O parametro offset tem de ser um numero inteiro";
            }
        }

        if (limit!=null)
        {
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
        }
        if (showAlert!=null)
        {
            if (showAlert.contentEquals("true") || showAlert.contentEquals("false")){
                alert=Boolean.parseBoolean(showAlert);
            }
            else{
                erroSintaxe=true;
                msg="O parametro showAlert tem de ser \"false\" ou \"true\"";
            }
        }
          
       
        if (!erroSintaxe)
        {
            for(int i = x; i <= y; i++){
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
        
                        if (species.equals("Alien") && alert) {
                            resp.getWriter().write("--------------------\n");
                            resp.getWriter().write("Um Alien foi encontrado morto com o ID "+i+"!\n");
                            resp.getWriter().write("--------------------\n");

                            JsonNode arrayEpisode = jsonNode.get("episode");
                            String urlNovo= arrayEpisode.get(arrayEpisode.size()-1).asText();
                            
                            request=HttpRequest.newBuilder()
                            .uri(URI.create(urlNovo))
                            .GET()
                            .build();
                            
                            response = client.send(request, HttpResponse.BodyHandlers.ofString());
                            jsonNode = mapper.readTree(response.body());
                    
                            String nameEp = jsonNode.get("name").asText();
                            resp.getWriter().write("--------------------\n");
                            resp.getWriter().write( "[ALERTA FORENSE] O ultimo registo do alien morto foi no episodio: "+nameEp+"\n");
                            resp.getWriter().write("--------------------\n");
                        } 
                    } else {
                        contDesc++;
                    }
                
                } catch (Exception e) {
                    System.out.println( "Erro a ler String json"+i);
                }
            
                
            }
            resp.getWriter().write("--------------------\n");
            resp.getWriter().write("Detetados "+contVivos+" personagens VIVOS e "+contMortos+" personagens MORTOS e "+contDesc+" com paredeiro desconhecido.\n");
            resp.getWriter().write("--------------------\n");
        }
        else
        {
            String jsonString = " \"status\": 400,\n" + //
                                "  \"error\": \"Bad Request\",\n" + //
                                "  \"message\": \""+msg+"\n";
            resp.setStatus(400);
            resp.getWriter().write("--------------------\n");
            resp.getWriter().write(jsonString);
            resp.getWriter().write("--------------------\n");
        }

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

