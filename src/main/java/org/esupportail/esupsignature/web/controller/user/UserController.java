package org.esupportail.esupsignature.web.controller.user;

import org.apache.commons.io.IOUtils;
import org.esupportail.esupsignature.entity.Document;
import org.esupportail.esupsignature.entity.User;
import org.esupportail.esupsignature.entity.User.EmailAlertFrequency;
import org.esupportail.esupsignature.entity.enums.SignType;
import org.esupportail.esupsignature.ldap.PersonLdap;
import org.esupportail.esupsignature.repository.BigFileRepository;
import org.esupportail.esupsignature.repository.DocumentRepository;
import org.esupportail.esupsignature.repository.SignBookRepository;
import org.esupportail.esupsignature.repository.UserRepository;
import org.esupportail.esupsignature.service.DocumentService;
import org.esupportail.esupsignature.service.SignBookService;
import org.esupportail.esupsignature.service.UserKeystoreService;
import org.esupportail.esupsignature.service.UserService;
import org.esupportail.esupsignature.service.file.FileService;
import org.esupportail.esupsignature.service.ldap.LdapPersonService;
import org.esupportail.esupsignature.service.sign.SignService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

@CrossOrigin(origins = "*")
@RequestMapping("user/users")
@Controller
@Scope(value="session")
@Transactional
public class UserController {

	private static final Logger logger = LoggerFactory.getLogger(UserController.class);

	@ModelAttribute("paramMenu")
	public String getActiveMenu() {
		return "active";
	}

	@Resource
	private UserRepository userRepository;
	
	@Resource
	private LdapPersonService ldapPersonService;

	@Resource
	private DocumentRepository documentRepository;
	
	@Resource
	private DocumentService documentService;
	
	@Resource
	private BigFileRepository bigFileRepository;
	
	@Resource
	private FileService fileService;

	@Resource
	private UserKeystoreService userKeystoreService;
	
	@Resource
	private UserService userService;

	@Resource
	private SignService signService;

	@Resource
	private SignBookRepository signBookRepository;
	
	@Resource
	private SignBookService signBookService;

	private String password;

	@ModelAttribute("password")
	public String getPassword() {
		return password;
	}

	long startTime;
	
	public void setPassword(String password) {
		startTime = System.currentTimeMillis();
		this.password = password;
	}

    @GetMapping
    public String createForm(Model model, @RequestParam(value = "referer", required=false) String referer, HttpServletRequest request) throws IOException, SQLException {
		User user = userService.getUserFromAuthentication();
		if(user != null) {
	        model.addAttribute("user", user);
        	model.addAttribute("signTypes", Arrays.asList(SignType.values()));
        	model.addAttribute("emailAlertFrequencies", Arrays.asList(EmailAlertFrequency.values()));
        	model.addAttribute("daysOfWeek", Arrays.asList(DayOfWeek.values()));
        	if(referer != null && !"".equals(referer) && !"null".equals(referer)) {
				model.addAttribute("referer", request.getHeader("referer"));
			}
			if(user.getSignImage() != null) {
				model.addAttribute("signFile", fileService.getBase64Image(user.getSignImage()));
			}
			return "user/users/update";
		} else {
			user = new User();
			model.addAttribute("user", user);
			return "user/users/create";
		}

    }
    
    @PostMapping
    public String create(Long id,
		    @RequestParam(value = "referer", required=false) String referer,
    		@RequestParam(value = "signImageBase64", required=false) String signImageBase64, 
    		@RequestParam(value = "emailAlertFrequency", required=false) EmailAlertFrequency emailAlertFrequency,
    		@RequestParam(value = "emailAlertHour", required=false) String emailAlertHour,
    		@RequestParam(value = "emailAlertDay", required=false) DayOfWeek emailAlertDay,
    		@RequestParam(value = "multipartKeystore", required=false) MultipartFile multipartKeystore, Model model) throws Exception {
        model.asMap().clear();
        User user = userRepository.findById(id).get();
		User userToUpdate = userService.getUserFromAuthentication();
        if(!multipartKeystore.isEmpty()) {
            if(userToUpdate.getKeystore() != null) {
            	bigFileRepository.delete(userToUpdate.getKeystore().getBigFile());
            	documentRepository.delete(userToUpdate.getKeystore());
            }
            userToUpdate.setKeystore(documentService.createDocument(multipartKeystore.getInputStream(), userToUpdate.getEppn() + "_cert.p12", multipartKeystore.getContentType()));
        }
        Document oldSignImage = userToUpdate.getSignImage();
        if(signImageBase64 != null && !signImageBase64.isEmpty()) {

        	userToUpdate.setSignImage(documentService.createDocument(fileService.base64Transparence(signImageBase64), userToUpdate.getEppn() + "_sign.png", "image/png"));
            if(oldSignImage != null) {
            	oldSignImage.getBigFile().getBinaryFile().getBinaryStream();
            	bigFileRepository.delete(oldSignImage.getBigFile());
            	documentRepository.delete(oldSignImage);
        	}
        }
    	userToUpdate.setEmailAlertFrequency(emailAlertFrequency);
    	userToUpdate.setEmailAlertHour(emailAlertHour);
    	userToUpdate.setEmailAlertDay(emailAlertDay);
    	if(referer != null && !"".equals(referer)) {
			return "redirect:" + referer;
		} else {
			return "redirect:/user/users/?form";
		}
    }
    
    @RequestMapping(value = "/view-cert", method = RequestMethod.GET, produces = "text/html")
    public String viewCert(@RequestParam(value =  "password", required = false) String password, RedirectAttributes redirectAttrs) throws Exception {
		User user = userService.getUserFromAuthentication();
		if (password != null && !"".equals(password)) {
        	setPassword(password);
        }
		try {
			logger.info(user.getKeystore().getInputStream().read() + "");
        	redirectAttrs.addFlashAttribute("messageCustom", userKeystoreService.checkKeystore(user.getKeystore().getInputStream(), this.password));
        } catch (Exception e) {
        	logger.error("open keystore fail", e);
        	this.password = "";
			startTime = 0;
        	redirectAttrs.addFlashAttribute("messageError", "Mauvais mot de passe");
		}
        return "redirect:/user/users/?form";
    }
    
	@RequestMapping(value = "/get-keystore-file", method = RequestMethod.GET)
	public void getSignedFile(HttpServletResponse response, Model model) {
		User user = userService.getUserFromAuthentication();
		Document userKeystore = user.getKeystore();
		try {
			response.setHeader("Content-Disposition", "inline;filename=\"" + userKeystore.getFileName() + "\"");
			response.setContentType(userKeystore.getContentType());
			IOUtils.copy(userKeystore.getInputStream(), response.getOutputStream());
		} catch (Exception e) {
			logger.error("get file error", e);
		}
	}

	@RequestMapping(value="/searchUser")
	@ResponseBody
	public List<PersonLdap> searchLdap(@RequestParam(value="searchString") String searchString, @RequestParam(required=false) String ldapTemplateName) {
		logger.debug("ldap search for : " + searchString);
		return userService.getPersonLdaps(searchString, ldapTemplateName);
   }

	@Scheduled(fixedDelay = 5000)
	public void clearPassword () {
		if(startTime > 0) {
			if(System.currentTimeMillis() - startTime > signService.getPasswordTimeout()) {
				password = "";
				startTime = 0;
			}
		}
	}
	
}