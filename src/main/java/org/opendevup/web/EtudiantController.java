package org.opendevup.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.validation.Valid;

import org.apache.commons.io.IOUtils;
import org.opendevup.dao.EtudiantRepository;
import org.opendevup.entities.Etudiant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping(value="/Etudiant")
public class EtudiantController {
	
	@Autowired // demande à Spring, lorsqu'il instancie le contrôleur, de chercher un objet qui implémente l'interface EtudiantRepository pour l'injecter
	private EtudiantRepository etudiantRepository;
	@Value("${dir.images}")
	private String imageDir;
	
	@RequestMapping(value="/index")
	public String index(Model model, 
			@RequestParam(name="page", defaultValue="0")int page,
			@RequestParam(name="keyword", defaultValue="")String keyword) {
		Page<Etudiant> pageEtudiants = etudiantRepository.chercherEtudiants("%"+keyword+"%", new PageRequest(page, 5));
		int pagesCount = pageEtudiants.getTotalPages();
		int[] pages = new int[pagesCount];
		for (int i = 0; i < pagesCount; i++) {
			pages[i] = i;
		}
		model.addAttribute("pages", pages);
		model.addAttribute("pageEtudiants", pageEtudiants);
		model.addAttribute("pageCourante", page);
		model.addAttribute("keyword", keyword);
		return "etudiants";
	}
	
	@RequestMapping(value="/form", method=RequestMethod.GET)
	public String formEtudiant(Model model) {
		model.addAttribute("etudiant", new Etudiant());
		return "formEtudiant";
	}
	
	@RequestMapping(value="/saveEtudiant", method=RequestMethod.POST)
	public String save(@Valid Etudiant etudiant, BindingResult bindingResult, @RequestParam(name="picture") MultipartFile file) throws IllegalStateException, IOException { // les erreurs éventuelles sont stockées dans bindingResult
		if (bindingResult.hasErrors()) {
			return "formEtudiant";
		}
		if (!(file.isEmpty())) {
			etudiant.setPhoto(file.getOriginalFilename());
		}
		etudiantRepository.save(etudiant);
		if (!(file.isEmpty())) {
			etudiant.setPhoto(file.getOriginalFilename());
			file.transferTo(new File(imageDir + etudiant.getId()));
		}
		return "redirect:index";
	}
	
	@RequestMapping(value="/getPhoto", produces=MediaType.IMAGE_JPEG_VALUE)
	@ResponseBody
	public byte[] getPhoto(Long id) throws FileNotFoundException, IOException {
		File file = new File(imageDir + id);
		return IOUtils.toByteArray(new FileInputStream(file));
	}
	
	@RequestMapping(value="/supprimer")
	public String supprimer(Long id) {
		etudiantRepository.delete(id);
		return "redirect:index";
	}
	
	@RequestMapping(value="/editer")
	public String editer(Long id, Model model) {
		Etudiant et = etudiantRepository.getOne(id);
		model.addAttribute("etudiant", et);
		return "editEtudiant";
	}
	
	@RequestMapping(value="/updateEtudiant", method=RequestMethod.POST)
	public String update(@Valid Etudiant etudiant, BindingResult bindingResult, @RequestParam(name="picture") MultipartFile file) throws IllegalStateException, IOException { // les erreurs éventuelles sont stockées dans bindingResult
		if (bindingResult.hasErrors()) {
			return "editEtudiant";
		}
		if (!(file.isEmpty())) {
			etudiant.setPhoto(file.getOriginalFilename());
		}
		etudiantRepository.save(etudiant);
		if (!(file.isEmpty())) {
			etudiant.setPhoto(file.getOriginalFilename());
			file.transferTo(new File(imageDir + etudiant.getId()));
		}
		return "redirect:index";
	}

}