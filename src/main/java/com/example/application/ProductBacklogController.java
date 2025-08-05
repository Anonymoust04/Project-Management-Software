package com.example.application;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import com.example.application.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.application.entity.CheckBox;
import com.example.application.entity.Filter;
import com.example.application.entity.SortWay;


@RestController
@RequestMapping("/api")
public class ProductBacklogController {

    @Autowired
    private static FirebaseService fireBaseService;

    @RequestMapping("/getUserDetails")
    public Task getExample(@RequestHeader() String id) throws InterruptedException, ExecutionException {
        return FirebaseService.getDetails(id);
    }
    

    @RequestMapping("/create")
    public static String createProductBacklog(@RequestBody() Task productBacklog) throws InterruptedException, ExecutionException{
        return FirebaseService.saveDetails(productBacklog);
    }

    @RequestMapping("/getProductBacklogTasks")
    public static List<Task> getProductBacklogTasks() throws InterruptedException, ExecutionException{
        return FirebaseService.getAllProductBacklogDetails();
    }

    @RequestMapping("/updateProductBacklogTask")
    public static String updateProductBacklogTask(Task productBacklog) throws InterruptedException, ExecutionException{
        return FirebaseService.updateProductBacklogTask(productBacklog);
    }

    @RequestMapping("/deleteProductBacklogTask")
    public static String deleteProductBacklogTask(Integer id) throws InterruptedException, ExecutionException{
        return FirebaseService.deleteProductBacklogTask(id);
    }

    @RequestMapping("/clearProductBacklogTask")
    public static String clearProductBacklogTask(Integer id) throws InterruptedException, ExecutionException{
        return FirebaseService.clearProductBacklogTask();
    }

    @RequestMapping("/createCheckBox")
    public static String createCheckBox(@RequestBody() CheckBox checkbox) throws InterruptedException, ExecutionException{
        return FirebaseService.saveCheckBox(checkbox);
    }

    @RequestMapping("/getAllCheckboxes")
    public static List<CheckBox> getAllCheckboxes() throws InterruptedException, ExecutionException{
        return FirebaseService.getCheckBoxDetails();
    }

    @RequestMapping("/updateSortWay")
    public static String updateSortWay(SortWay sortWay) throws InterruptedException, ExecutionException{
        return FirebaseService.saveSortWay(sortWay);
    }

    @RequestMapping("/getSortWay")
    public static SortWay getSortWay() throws InterruptedException, ExecutionException{
        return FirebaseService.getSortWay();
    } 
    
    @RequestMapping("/updateFilter")
    public static String updateFilter(Set<String> filterSet) throws InterruptedException, ExecutionException{
        return FirebaseService.saveFilter(filterSet);
    }

    @RequestMapping("/getFilter")
    public static Filter getFilter() throws InterruptedException, ExecutionException{
        return FirebaseService.getFilter();
    } 
}
