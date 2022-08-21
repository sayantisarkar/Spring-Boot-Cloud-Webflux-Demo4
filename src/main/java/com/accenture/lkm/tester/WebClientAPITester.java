package com.accenture.lkm.tester;
import java.io.IOException;

import org.springframework.http.ResponseEntity;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.accenture.lkm.model.EmployeeBean;

public class WebClientAPITester {
    static private WebClient webClient;
    static private String employees="employees";
    static private String saveEmployee="saveEmployee";
    static private String employeeDelete ="employee/";
    static private String employeeById = "employee/";
    static private String employeeUpdate="updateEmployee";
    static private String baseURL="http://localhost:8095/";
    
    static {
    	
        webClient =WebClient.builder()
        	
                .baseUrl(baseURL)
                .build();        
    }

    public static void main(String args[]) throws IOException {
        /**
         * CRUD operations. Uncomment one by one and execute.  
         * */
        
    	//1: Creating Employee
        //displayResultOfMono(createEmployee());
        
    	//2: Display All Employee
    	displayResultOfFlux(getAllEmployees());
    	
    	//3: Find Employee By Id
        //displayResultOfMono(findEmployeeById(1003));
    	
    	//4: Update Employee By Id
        //displayResultOfMono(updateEmployeeById(1001,"Jacob",23234.6,"10"));
        
    	//5: Delete Employee By Id
        //displayResultOfMono(deleteEmployeeById(1002));
    	
    	//to stop the execution till the asynch call runs.
    	// else main will terminate before printing the results
    	System.in.read();
    }

    /**
     * Used to Subscribe to a Mono.
     * if there is an error then prints the same and terminates.
     * else when ever publisher publishes all the values and successfully completes,
     * then  terminates.
     * */
    static private void displayResultOfMono(Mono<?> param){
    	param
    	.subscribe(null, // no consumer as doOnSuccess is used
		    			exception->{
		    				System.out.println("Got an Exception terminated: "+exception.getMessage());
							System.exit(0);	
						},  
						()->{
								System.out.println("Completed");
								System.exit(0);	
							}
						);
    }
    
    /**
     * Used to Subscribe to a Flux.
     * if there is an error then prints the same and terminates.
     * else when ever publisher publishes all the values and successfully completes,
     * then  terminates.
     * */
    static private void displayResultOfFlux(Flux<?> param){
    	param
    	.subscribe(null, // no consumer as doOnSuccess is used
    			exception->{
    				System.out.println("Got an Exception terminated: "+exception.getMessage());
    				exception.printStackTrace();
					System.exit(0);	
				},  
				()->{
						System.out.println("Completed");
						System.exit(0);	
					}
				);
    }
    
    
    static private Mono<ResponseEntity<String>> createEmployee() {
        return webClient
                .post() //Request Method
                .uri(saveEmployee)//Url on which Request has to be send
                .body(Mono.just(new EmployeeBean(null, "MSD12", 1.99,"1002")), EmployeeBean.class)// Body
                .exchange() // Placing Request using Exchange
                .flatMap(response -> response.toEntity(String.class)) //Processing Response, Converting to Mono<ResponseEntity<>>
                .doOnSuccess(result -> 
                						{
                							System.out.println("Employee got created: " + result.getStatusCodeValue());
                							System.out.println("Employee got created: " + result.getBody());
                						}); 
        								// Call Back Event
        /** here response.toEntity(EmployeeBean.class) gives Mono of its own,
			* we need to covert current response which also is of Mono type. 
			* This conversion needs to be done to target type: Mono<ResponseEntity>
			* if not used flatMap (and used map) then it will become Mono<Mono<ResponseEntity>>
			*/
    }

    static private Flux<EmployeeBean> getAllEmployees() {
        return webClient
                .get() //Request Method
                .uri(employees)
                .retrieve() //Placing the Request
                .bodyToFlux(EmployeeBean.class) //Processing REQ: Converting to Flux<EmployeeBean>
                .doOnNext(result -> System.out.println(result)); 
        		//Call Back Event to print result	
    }

    static private Mono<EmployeeBean> updateEmployeeById(Integer id, String name, double salary,String deptCode) {
        return webClient
                .put() //Request Method
                .uri(employeeUpdate)//Sending Body
                .body(Mono.just(new EmployeeBean(id, name, salary,deptCode)), EmployeeBean.class)
                .retrieve()//Placing the Request
                .bodyToMono(EmployeeBean.class)//Processing REQ: Converting to Mono
                .doOnSuccess(result -> System.out.println("Employee updated: "+ result));
                
    }

    static private Mono<EmployeeBean> deleteEmployeeById(Integer id) {
        return webClient
                .delete()//Request Method
                .uri(employeeDelete+id)//Appending the Id to delete the employee with specific Id
                .retrieve()//Placing the Request
                .bodyToMono(EmployeeBean.class)//Processing REQ: Converting to Mono
                .doOnSuccess(result -> System.out.println("Employee deleted: " + result));
               
    }

    static private Mono<EmployeeBean> findEmployeeById(Integer id) {
        return webClient
                .get()//Request Method
                .uri(employeeById+ id)//Appending the Id to find the employee with specific Id
                .retrieve()//Placing the Request
                .bodyToMono(EmployeeBean.class)//Processing REQ: Converting to Flux
                .doOnSuccess(result -> System.out.println("Employee Found: " + result));

    }

}
