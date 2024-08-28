package br.com.caju.transaction_authorizer.application.service;

import br.com.caju.transaction_authorizer.domain.entity.Category;
import br.com.caju.transaction_authorizer.domain.repository.IMerchantRepository;
import br.com.caju.transaction_authorizer.domain.entity.Merchant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CategoryService {
    public static final Category FALLBACK_CATEGORY = Category.CASH;
    public static final Map<String, Category> MERCHANT_CATEGORY_CODE_MAP = Map.of(
            "5411", Category.FOOD,
            "5412", Category.FOOD,
            "5811", Category.MEAL,
            "5812", Category.MEAL
    );

    final IMerchantRepository merchantRepository;

    @Autowired
    public CategoryService(IMerchantRepository merchantRepository) {
        this.merchantRepository = merchantRepository;
    }

    public Set<Category> findCategoriesWithFallback(String merchantName, String mcc) {
        return Stream.of(
                        findMerchantCategory(merchantName),
                        findMccCategory(mcc),
                        Optional.of(FALLBACK_CATEGORY)
                )
                .flatMap(Optional::stream)
                .collect(Collectors.toCollection(LinkedHashSet::new));
    }

    private Optional<Category> findMerchantCategory(String merchantName) {
        return merchantRepository.findByName(merchantName).map((Merchant::getCategory));
    }

    private Optional<Category> findMccCategory(String mcc) {
        return Optional.ofNullable(MERCHANT_CATEGORY_CODE_MAP.get(mcc));
    }
}
