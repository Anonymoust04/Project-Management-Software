package com.example.application;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

import com.example.application.entity.*;

import com.example.application.entity.Filter;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Service;

import com.google.api.core.ApiFuture;
import com.google.firebase.internal.NonNull;

@Service
@DependsOn("initialise")
public class FirebaseService {
 
    @NonNull
    private static final String COLLECTION_NAME = "productBacklogs";

    // Product Backlog Task
    public static String saveDetails(Task productBacklog) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("productBacklogs").document(productBacklog.getTaskId().toString()).set(productBacklog);

        dbFirestore.collection("productBacklogs").orderBy("taskId");
        
        dbFirestore.shutdown();

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public static Task getDetails(String id) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("productBacklogs").document(id);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Task productBacklog;

        dbFirestore.shutdown();

        if (document.exists()){
            productBacklog = document.toObject(Task.class);
            return productBacklog;
        }else{
            return null;
        }
    }

    public static ArrayList<Task> getAllProductBacklogDetails() throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        Iterable<DocumentReference> documentReference = dbFirestore.collection("productBacklogs").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        ArrayList<Task>  productBacklogList = new ArrayList<Task>();
        Task productBacklogTask;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            productBacklogTask = document.toObject(Task.class);
            productBacklogList.add(productBacklogTask);
        }
        dbFirestore.shutdown();
        return productBacklogList;
    }

    public static String updateProductBacklogTask(Task productBacklog) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        if (dbFirestore == null){
            return null;
        }
        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("productBacklogs").document(productBacklog.getTaskId().toString()).set(productBacklog);

        dbFirestore.collection("productBacklogs").orderBy("taskId");
        
        dbFirestore.shutdown();
        
        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public static String deleteProductBacklogTask(Integer id) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("productBacklogs").document(id.toString()).delete();


        dbFirestore.collection("productBacklogs").orderBy("taskId");

        dbFirestore.shutdown();

        return "Product Backlog Task " + id.toString() + "has been deleted sucessfully." ;
    }

    public static String clearProductBacklogTask() throws ExecutionException, InterruptedException{
        Firestore dbFirestore = FirebaseInit.initialise();
        ApiFuture<QuerySnapshot> query = dbFirestore.collection("productBacklogs").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete();
          }
        dbFirestore.shutdown();
        return "All tasks in Product Backlog has been cleared.";
    }


    // Checkbox for Admin Dashboard
    public static String saveCheckBox(CheckBox checkbox) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("checkBox").document(checkbox.getLabelElement()).set(checkbox);
        dbFirestore.shutdown();
        return ("Create time: " + result.get().getUpdateTime().toString());
    }

    public static CheckBox getCheckBox(String labelElement) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("checkBox").document(labelElement);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        CheckBox checkBox;

        dbFirestore.shutdown();

        if (document.exists()){
            checkBox = document.toObject(CheckBox.class);
            return checkBox;
        }else{
            return null;
        }
    }

    public static String deleteCheckBox(String labelElement) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("checkBox").document(labelElement).delete();

        dbFirestore.shutdown();

        return "Checklist Task " + labelElement + "has been deleted sucessfully.";
    }

    public static ArrayList<CheckBox> getCheckBoxDetails() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        if (dbFirestore == null) {
            return null;
        }
        ApiFuture<QuerySnapshot> query = dbFirestore.collection("checkBox").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        ArrayList<CheckBox>  checkBoxList = new ArrayList<CheckBox>();
        CheckBox checkBox;

        for (QueryDocumentSnapshot document : documents) {
            checkBox = document.toObject(CheckBox.class);
            checkBoxList.add(checkBox);
        }
        dbFirestore.shutdown();
        return checkBoxList;
    }


    // Sort way for Product Backlog
    public static String saveSortWay(SortWay sortWay) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        CollectionReference collection = dbFirestore.collection("sortWay");
        ApiFuture<QuerySnapshot> query = collection.get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete();
          }

        ApiFuture<WriteResult> updateSortWay = dbFirestore.collection("sortWay").document("sortWay").set(sortWay);
        dbFirestore.shutdown();
        return  "Updated Sorting at" + updateSortWay.get().getUpdateTime().toString();
    }

    public static SortWay getSortWay() throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("sortWay").document("sortWay");
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        SortWay sortWay;

        dbFirestore.shutdown();

        if (document.exists()){
            sortWay = document.toObject(SortWay.class);
            return sortWay;
        }else{
            return null;
        }
    }

    // Filter for Product Backlog
    public static String saveFilter(Set<String> filterSet) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        CollectionReference collection = dbFirestore.collection("filter");
        ApiFuture<QuerySnapshot> query = collection.get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete();
        }
        ArrayList<String> filterList = new ArrayList<>(filterSet);
        HashMap<String, ArrayList<String>> hash = new HashMap<>();
        hash.put("filterList", filterList);

        ApiFuture<WriteResult> updateFilter = dbFirestore.collection("filter").document("filterList").set(hash);
        dbFirestore.shutdown();
        return  "Updated Filtering at" + updateFilter.get().getUpdateTime().toString();
    }

    public static Filter getFilter() throws InterruptedException, ExecutionException {
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("filter").document("filterList");
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Filter filter;

        dbFirestore.shutdown();

        if (document.exists()) {
            filter = document.toObject(Filter.class);
            return filter;
        } else {
            return null;
        }
    }

    // Role for Sprint Board
    public static String saveRole(Role role) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("roles").document(role.getName()).set(role);
        dbFirestore.shutdown();
        return ("Create time: " + result.get().getUpdateTime().toString());
    }

    public static Role getRole(String name) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("roles").document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Role role;

        dbFirestore.shutdown();

        if (document.exists()){
            role = document.toObject(Role.class);
            return role;
        }else{
            return null;
        }
    }

    public static String deleteRole(String name) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("roles").document(name).delete();

        dbFirestore.shutdown();

        return "Role name: " + name + "has been deleted sucessfully.";
    }

    public static ArrayList<Role> getRoleDetails() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        if (dbFirestore == null) {
            return null;
        }
        ApiFuture<QuerySnapshot> query = dbFirestore.collection("roles").get();

        QuerySnapshot querySnapshot = query.get();
        List<QueryDocumentSnapshot> documents = querySnapshot.getDocuments();

        ArrayList<Role>  roleList = new ArrayList<Role>();
        Role role;

        for (QueryDocumentSnapshot document : documents) {
            role = document.toObject(Role.class);
            roleList .add(role);
        }
        dbFirestore.shutdown();
        return roleList;
    }

    // Sprint for Sprint Board
    public static String saveSprint(Sprint newSprint) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("sprint").document(newSprint.getName()).set(newSprint);

        dbFirestore.collection("sprint").orderBy("name");

        dbFirestore.shutdown();

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public static Sprint getSprint(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("sprint").document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        Sprint sprint = null;

        if (document.exists()) {
            sprint = document.toObject(Sprint.class);
        }

        dbFirestore.shutdown();

        return sprint;
    }

    public static ArrayList<Sprint> getAllSprintDetails() throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        Iterable<DocumentReference> documentReference = dbFirestore.collection("sprint").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        ArrayList<Sprint>  sprintList = new ArrayList<Sprint>();
//        Sprint sprint;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            Sprint sprint = document.toObject(Sprint.class);
            sprintList.add(sprint);
        }
        dbFirestore.shutdown();
        return sprintList;
    }

    public static String updateSprint(Sprint updatedSprint) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("sprint").document(updatedSprint.getName()).set(updatedSprint);

        dbFirestore.collection("sprint").orderBy("name");

        dbFirestore.shutdown();

        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public static String deleteSprint(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("sprint").document(name).delete();

        dbFirestore.shutdown();

        return "Sprint " + name + "has been deleted sucessfully." ;
    }

    public static String createSprintUserStoryFromPB(SprintBoardUserStory newUserStory) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        Sprint selectedSprint = FirebaseService.getSprint(newUserStory.getSprintBoardName());

        if (selectedSprint != null) {
            selectedSprint.addUserStory(newUserStory);
        };

        String message = FirebaseService.updateSprint(selectedSprint);

        dbFirestore.shutdown();

        return message;
    }

    public static String setUser(UserInfo newUserInfo) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("accounts").document(newUserInfo.getUsername()).set(newUserInfo);

        dbFirestore.collection("accounts").orderBy("username");

        dbFirestore.shutdown();

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public static String saveSprintBacklogUserStory(SprintBacklogUserStory newUserStory) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("sprintBacklog").document(newUserStory.getTaskName()).set(newUserStory);

        dbFirestore.shutdown();

        return collectionsApiFuture.get().getUpdateTime().toString();
    }

    public static String updateSprintBacklogUserStory(SprintBacklogUserStory userStory) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> collectionApiFuture = dbFirestore.collection("sprintBacklog").document(userStory.getTaskName()).set(userStory);

        // dbFirestore.collection("sprint").orderBy("name");

        dbFirestore.shutdown();

        return collectionApiFuture.get().getUpdateTime().toString();
    }

    public static String deleteSprintBacklogUserStory(String userStory) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("sprintBacklog").document(userStory).delete();

        dbFirestore.shutdown();

        return "Sprint Task" + userStory + "has been deleted sucessfully." ;
    }

    public static ArrayList<SprintBacklogUserStory> getAllSprintBacklogUserStories() throws ExecutionException, InterruptedException, Exception {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        Iterable<DocumentReference> documentReference = dbFirestore.collection("sprintBacklog").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        ArrayList<SprintBacklogUserStory>  sprintBacklogUserStoryList = new ArrayList<SprintBacklogUserStory>();
        SprintBacklogUserStory sprintBacklogUserStory;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            sprintBacklogUserStory = document.toObject(SprintBacklogUserStory.class);
            sprintBacklogUserStoryList.add(sprintBacklogUserStory);
        }
        return sprintBacklogUserStoryList;
    }

    public static String clearAllSprintBacklogUserStories() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        ApiFuture<QuerySnapshot> query = dbFirestore.collection("sprintBacklog").get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();
        for (QueryDocumentSnapshot document : documents) {
            document.getReference().delete();
        }
        dbFirestore.shutdown();
        return "Sprint backlog has been cleared.";
    }

    public static UserInfo getUser(String userName) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();
        DocumentReference documentReference = dbFirestore.collection("accounts").document(userName);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        UserInfo user;

        dbFirestore.shutdown();

        if (document.exists()){
            user = document.toObject(UserInfo.class);
            return user;
        }else{
            return null;
        }
    }

    public static ArrayList<UserInfo> getAllUserDetails() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        Iterable<DocumentReference> documentReference = dbFirestore.collection("accounts").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        ArrayList<UserInfo>  userInfoList = new ArrayList<UserInfo>();
        UserInfo userInfo;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            userInfo = document.toObject(UserInfo.class);
            userInfoList.add(userInfo);
        }
        dbFirestore.shutdown();
        return userInfoList;
    }

    public static String deleteUser(String userName) throws InterruptedException, ExecutionException{
        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        ApiFuture<WriteResult> result = dbFirestore.collection("accounts").document(userName).delete();

        dbFirestore.shutdown();

        return "Username: " + userName + " has been deleted sucessfully.";
    }

    // for "move to sprint"
    public static ArrayList<String> getAllSprintNames() throws ExecutionException, InterruptedException {

        Firestore dbFirestore = FirebaseInit.initialise();

        if (dbFirestore == null){
            return null;
        }

        Iterable<DocumentReference> documentReference = dbFirestore.collection("sprint").listDocuments();
        Iterator<DocumentReference> iterator = documentReference.iterator();

        ArrayList<String>  sprintNameList = new ArrayList<String>();
        Sprint sprint;

        while(iterator.hasNext()){
            DocumentReference documentReference1 = iterator.next();
            ApiFuture<DocumentSnapshot> future = documentReference1.get();
            DocumentSnapshot document = future.get();

            sprint = document.toObject(Sprint.class);
            assert sprint != null;
            if (!sprint.isActive()) {
                sprintNameList.add(sprint.getName());
            };
        }
        dbFirestore.shutdown();
        return sprintNameList;
    }

    public static void addUserStoryToSprint(String sprintName, SprintBoardUserStory userStory) throws ExecutionException, InterruptedException {
        Firestore db = FirebaseInit.initialise();

        ApiFuture<QuerySnapshot> query = db.collection("sprint").whereEqualTo("name", sprintName).get();
        List<QueryDocumentSnapshot> documents = query.get().getDocuments();

        if (!documents.isEmpty()) {
            DocumentReference sprintRef = documents.get(0).getReference();

            // Get the current list of user stories
            ApiFuture<DocumentSnapshot> future = sprintRef.get();
            DocumentSnapshot document = future.get();
            Sprint sprint = document.toObject(Sprint.class);

            if (sprint != null) {
                List<SprintBoardUserStory> currentStories = sprint.getUserStories();
                currentStories.add(userStory);

                // Update the sprint with the new list of user stories
                sprintRef.update("userStories", currentStories);
            }
        } else {
            throw new IllegalArgumentException("Sprint not found: " + sprintName);
        }

        db.shutdown();
    }





}
