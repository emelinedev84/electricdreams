package com.devnoir.blog.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devnoir.blog.enums.Language;
import com.devnoir.blog.repositories.PostContentRepository;
import com.devnoir.blog.utils.SlugifyHelper;

@Service
public class UrlHandleService {

	private static final int MAX_LEN = 70;
	
	@Autowired
	private PostContentRepository contentRepository;

	public UrlHandleService(PostContentRepository contentRepository) {
		this.contentRepository = contentRepository;
	}
	
	public String generateUniqueHandle(String title, Language language) {
        String base = SlugifyHelper.toSlug(title);
        if (base == null || base.isBlank()) base = "post";

        // 1) tenta base puro
        if (!contentRepository.existsByUrlHandleAndLanguage(base, language)) {
            return base;
        }

        // 2) tenta com sufixo incremental
        for (int i = 2; i < 10_000; i++) { // limite bem alto só pra evitar loop infinito
            String suffix = "-" + i;
            String candidate = withSuffix(base, suffix);

            if (!contentRepository.existsByUrlHandleAndLanguage(candidate, language)) {
                return candidate;
            }
        }

        // se chegar aqui, algo muito estranho aconteceu
        throw new IllegalStateException("Could not generate unique urlHandle for title: " + title);
    }
	
	public String generateUniqueHandleForUpdate(String title, Language language, Long contentId) {
        String base = SlugifyHelper.toSlug(title);
        if (base == null || base.isBlank()) base = "post";

        if (!contentRepository.existsByUrlHandleAndLanguageAndIdNot(base, language, contentId)) {
            return base;
        }

        for (int i = 2; i < 10_000; i++) { 
            String suffix = "-" + i;
            String candidate = withSuffix(base, suffix);

            if (!contentRepository.existsByUrlHandleAndLanguageAndIdNot(candidate, language, contentId)) {
                return candidate;
            }
        }

        // se chegar aqui, algo muito estranho aconteceu
        throw new IllegalStateException("Could not generate unique urlHandle for title: " + title);
    }
	
	private String withSuffix(String base, String suffix) {
        int allowed = MAX_LEN - suffix.length();
        String trimmedBase = base.length() > allowed ? base.substring(0, allowed) : base;

        // tira hífen sobrando no final
        trimmedBase = trimmedBase.replaceAll("-+$", "");
        if (trimmedBase.isBlank()) trimmedBase = "post";

        return trimmedBase + suffix;
    }
}
