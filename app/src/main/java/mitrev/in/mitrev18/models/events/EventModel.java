package mitrev.in.mitrev18.models.events;


import io.realm.RealmObject;

public class EventModel extends RealmObject {

    private String eventName;
    private String eventId;
    private String description;
    private String eventMaxTeamNumber;
    private String catName;
    private String catId;
    private String round;
    private String venue;
    private String startTime;
    private String endTime;
    private String day;
    private String date;
    private String contactName;
    private String contactNumber;
    private String eventType;
    private String hashtag;

    public EventModel() {
    }

    public EventModel(EventDetailsModel eventDetails, ScheduleModel schedule){

        if (eventDetails != null){
            eventName = eventDetails.getEventName();
            eventId = eventDetails.getEventID();
            description = eventDetails.getDescription();
            eventMaxTeamNumber = eventDetails.getMaxTeamSize();
            catName = eventDetails.getCatName();
            catId = eventDetails.getCatID();
            contactName = eventDetails.getContactName();
            contactNumber = eventDetails.getContactNo();
            eventType = eventDetails.getType();
            hashtag = eventDetails.getHash();
        }

        if (schedule != null){
            venue = schedule.getVenue();

            if (schedule.getStartTime().contains(".")){
                startTime = schedule.getStartTime().replace('.', ':');
            }
            else{
                startTime = schedule.getStartTime();
            }

            if (schedule.getEndTime().contains(".")){
                endTime = schedule.getEndTime().replace('.', ':');
            }
            else{
                endTime = schedule.getEndTime();
            }

            day = schedule.getDay();
            date = schedule.getDate();
            round = schedule.getRound();
        }
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public String getHashtag() {
        return hashtag;
    }

    public void setHashtag(String hashtag) {
        this.hashtag = hashtag;
    }

    public String getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(String contactNumber) {
        this.contactNumber = contactNumber;
    }

    public String getContactName() {
        return contactName;
    }

    public void setContactName(String contactName) {
        this.contactName = contactName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getVenue() {
        return venue;
    }

    public void setVenue(String venue) {
        this.venue = venue;
    }

    public String getCatId() {
        return catId;
    }

    public void setCatId(String catId) {
        this.catId = catId;
    }

    public String getCatName() {
        return catName;
    }

    public void setCatName(String catName) {
        this.catName = catName;
    }

    public String getEventMaxTeamNumber() {
        return eventMaxTeamNumber;
    }

    public void setEventMaxTeamNumber(String eventMaxTeamNumber) {
        this.eventMaxTeamNumber = eventMaxTeamNumber;
    }

    public String getRound() {
        return round;
    }

    public void setRound(String round) {
        this.round = round;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventName() {
        return eventName;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }
}

