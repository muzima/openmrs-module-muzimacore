package org.openmrs.module.muzima.model;

import org.openmrs.BaseOpenmrsData;
import org.openmrs.Person;

public class MessageData extends BaseOpenmrsData implements Data{


    private String source;

    private Person sender;

    private Person receiver;

    private String subject;

    private String body;

    private String senderDate;

    private String senderTime;

    private Boolean voided;

    @Override
    public Integer getId() {
        return null;
    }

    @Override
    public void setId(Integer integer) {

    }

    /**
     * Sets the source of the message as either mUzima or mPHR or other predetermined
     * source.
     * @return String source
     */
    public String getSource() {
        return source;
    }

    /**
     * Set the source of the message which can be a mobile device (mUzima or mPHR).
     * @param source String
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * Obtains the Sender of the Message as an org.openmrs.Person instance.
     * @return org.openmrs.Person
     */
    public Person getSender() {
        return sender;
    }

    /**
     * Sets the sender of the Message as a Person instance.
     * @param sender org.openmrs.Person
     */
    public void setSender(Person sender) {
        this.sender = sender;
    }

    /**
     * Obtain the reciver of the message as a person instance.
     * @return org.openmrs.Person
     */
    public Person getReceiver() {
        return receiver;
    }

    /**
     * Sets the receiver of the message as an extension of Person
     * @param receiver
     */
    public void setReceiver(Person receiver) {
        this.receiver = receiver;
    }

    /**
     * Obtains the subject of this message.
     * @return Subject - String
     */
    public String getSubject() {
        return subject;
    }

    /**
     * Set the message subject
     * @param subject String
     */
    public void setSubject(String subject) {
        this.subject = subject;
    }

    /**
     * Obtain the content of the message.
     * @return String - message body
     */
    public String getBody() {
        return body;
    }

    /**
     * Sets the body of the message. (message content).
     * @param body
     */
    public void setBody(String body) {
        this.body = body;
    }

    /**
     * Ontains the Date message was sent as a string representation.
     * @return
     */
    public String getSenderDate() {
        return senderDate;
    }

    /**
     * Set the date the message was sent as a string representation.
     * @param senderDate String
     */
    public void setSenderDate(String senderDate) {
        this.senderDate = senderDate;
    }

    /**
     * Ontains the time message was sent as a string representation.
     * @return
     */
    public String getSenderTime() {
        return senderTime;
    }

    /**
     * Sets the sender of the message.
     * @param senderTime String time representation
     */
    public void setSenderTime(String senderTime) {
        this.senderTime = senderTime;
    }

    /**
     * Obtain the voiding status of this message.
     * @return Boolean
     */
    @Override
    public Boolean getVoided() {
        return voided;
    }

    /**
     * Set the voiding status of the message.
     *
     * @param voided
     */
    @Override
    public void setVoided(Boolean voided) {
        this.voided = voided;
    }

    /**
     * Get the data payload of this data.
     *
     * @return the payload of this data.
     */
    @Override
    public String getPayload() {
        return null;
    }
}
