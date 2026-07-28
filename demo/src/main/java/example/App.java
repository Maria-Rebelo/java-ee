package example;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) 
    {
        String jsonString = "{\"id\":\"1\", \"nome\":\"Rodrigo\"}";
        ObjectMapper mapper = new ObjectMapper();
        System.out.println( "Hello World!" );
        try {
            JsonNode jsonNode = mapper.readTree(jsonString);
            int id = jsonNode.get("id").asInt();
            String nome = jsonNode.get("nome").asText();
   
            System.out.println("Id: " + id);
            System.out.println("Nome: " + nome);
            
        } catch (JsonProcessingException pe) {
            System.out.println( "Erro a ler String json" );
        }
    }
}
