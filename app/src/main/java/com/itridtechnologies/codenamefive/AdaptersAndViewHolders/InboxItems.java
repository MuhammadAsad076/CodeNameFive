package com.itridtechnologies.codenamefive.AdaptersAndViewHolders;

public class InboxItems {

    private String InboxMessage;
    private String dateOfMessage;
    private String timeOfMessage;

    public InboxItems(String inboxMessage, String dateOfMessage, String timeOfMessage) {
        InboxMessage = inboxMessage;
        this.dateOfMessage = dateOfMessage;
        this.timeOfMessage = timeOfMessage;
    }//constructor

    public String getInboxMessage() {
        return InboxMessage;
    }

    public String getDateOfMessage() {
        return dateOfMessage;
    }

    public String getTimeOfMessage() {
        return timeOfMessage;
    }
}//end class
