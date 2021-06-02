package org.esupportail.esupsignature.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.esupportail.esupsignature.entity.enums.EmailAlertFrequency;
import org.esupportail.esupsignature.entity.enums.UiParams;
import org.esupportail.esupsignature.entity.enums.UserType;

import javax.persistence.*;
import java.time.DayOfWeek;
import java.util.*;

@Entity
@Table(name = "user_account")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class User {
	
	@Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

	@Version
    private Integer version;
	
	private String name;
	
	private String firstname;
	
    @Column(unique=true)
    private String eppn;

    @Column(unique=true)
    private String email;

    @ElementCollection(targetClass=String.class)
    private List<String> managersRoles = new ArrayList<>();

    @ElementCollection
    @JsonIgnore
    private Map<UiParams, String> uiParams = new LinkedHashMap<>();

    private String formMessages = "";

    @Enumerated(EnumType.STRING)
    private UserType userType;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.REMOVE, orphanRemoval = true)
    @OrderColumn
    private List<Document> signImages = new ArrayList<>();

    private Integer defaultSignImageNumber = 0;

    @Transient
    private String ip;

    @Transient
    private String keystoreFileName;

    @Transient
    private List<Long> signImagesIds;

    @Transient
    private String signImageBase64;

    @Transient
    private Long userShareId;

    @JsonIgnore
    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.REMOVE, orphanRemoval = true)
    private Document keystore = new Document();

    @Enumerated(EnumType.STRING)
    private EmailAlertFrequency emailAlertFrequency = EmailAlertFrequency.immediately;

    private Integer emailAlertHour;
    
    @Enumerated(EnumType.STRING)
    private DayOfWeek emailAlertDay;

    @Temporal(TemporalType.TIMESTAMP)
    private Date lastSendAlertDate = new Date(0);

    public EmailAlertFrequency getEmailAlertFrequency() {
        return this.emailAlertFrequency;
    }

    public void setEmailAlertFrequency(EmailAlertFrequency emailAlertFrequency) {
        this.emailAlertFrequency = emailAlertFrequency;
    }

    @ElementCollection
    private List<String> roles = new ArrayList<>();

	public Long getId() {
        return this.id;
    }

	public void setId(Long id) {
        this.id = id;
    }

    public Integer getVersion() {
        return version;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

	public String getName() {
        return this.name;
    }

	public void setName(String name) {
        this.name = name;
    }

	public String getFirstname() {
        return this.firstname;
    }

	public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

	public String getEppn() {
        return this.eppn;
    }

	public void setEppn(String eppn) {
        this.eppn = eppn;
    }

	public String getEmail() {
        return this.email;
    }

	public void setEmail(String email) {
        this.email = email;
    }

    public List<String> getManagersRoles() {
        return managersRoles;
    }

    public void setManagersRoles(List<String> managersRoles) {
        this.managersRoles = managersRoles;
    }

    public Map<UiParams, String> getUiParams() {
        return uiParams;
    }

    public void setUiParams(Map<UiParams, String> uiParams) {
        this.uiParams = uiParams;
    }

    public String getFormMessages() {
        return formMessages;
    }

    public void setFormMessages(String formMessages) {
        this.formMessages = formMessages;
    }

    public UserType getUserType() {
        return userType;
    }

    public void setUserType(UserType userType) {
        this.userType = userType;
    }

    public List<Document> getSignImages() {
        return signImages;
    }

    public void setSignImages(List<Document> signImages) {
        this.signImages = signImages;
    }

    public String getIp() {
        return this.ip;
    }

	public void setIp(String ip) {
        this.ip = ip;
    }

    public String getKeystoreFileName() {
        return keystoreFileName;
    }

    public void setKeystoreFileName(String keystoreFileName) {
        this.keystoreFileName = keystoreFileName;
    }

    public List<Long> getSignImagesIds() {
        return signImagesIds;
    }

    public void setSignImagesIds(List<Long> signImagesIds) {
        this.signImagesIds = signImagesIds;
    }

    public String getSignImageBase64() {
        return this.signImageBase64;
    }

	public void setSignImageBase64(String signImageBase64) {
        this.signImageBase64 = signImageBase64;
    }

    public Long getUserShareId() {
        return userShareId;
    }

    public void setUserShareId(Long userShareId) {
        this.userShareId = userShareId;
    }

    public Document getKeystore() {
        return this.keystore;
    }

	public void setKeystore(Document keystore) {
        this.keystore = keystore;
    }

	public Integer getEmailAlertHour() {
        return this.emailAlertHour;
    }

	public void setEmailAlertHour(Integer emailAlertHour) {
        this.emailAlertHour = emailAlertHour;
    }

	public DayOfWeek getEmailAlertDay() {
        return this.emailAlertDay;
    }

	public void setEmailAlertDay(DayOfWeek emailAlertDay) {
        this.emailAlertDay = emailAlertDay;
    }

	public Date getLastSendAlertDate() {
        return this.lastSendAlertDate;
    }

	public void setLastSendAlertDate(Date lastSendAlertDate) {
        this.lastSendAlertDate = lastSendAlertDate;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public Integer getDefaultSignImageNumber() {
        return defaultSignImageNumber;
    }

    public void setDefaultSignImageNumber(Integer defaultSignImageNumber) {
        this.defaultSignImageNumber = defaultSignImageNumber;
    }
}
