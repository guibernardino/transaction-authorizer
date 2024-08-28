package br.com.caju.transaction_authorizer.application.service;

import br.com.caju.transaction_authorizer.domain.entity.Category;
import br.com.caju.transaction_authorizer.domain.entity.Merchant;
import br.com.caju.transaction_authorizer.domain.repository.IMerchantRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class CategoryServiceTest {

    @Mock
    private IMerchantRepository merchantRepository;

    @InjectMocks
    private CategoryService categoryService;

    @Test
    void testFindCategoriesWithFallback_MccAndMerchantCategoryFound() {
        String merchantName = "Test Merchant";
        String mcc = "5411";

        when(merchantRepository.findByName(merchantName))
                .thenReturn(Optional.of(new Merchant(1L, merchantName, Category.MEAL)));

        Set<Category> categories = categoryService.findCategoriesWithFallback(merchantName, mcc);

        Set<Category> expectedCategories = new LinkedHashSet<>();
        expectedCategories.add(Category.MEAL); // from merchant
        expectedCategories.add(Category.FOOD); // from mcc
        expectedCategories.add(Category.CASH); // fallback

        assertEquals(expectedCategories, categories, "Should return categories from merchant, MCC, and fallback");
    }

    @Test
    void testFindCategoriesWithFallback_OnlyMerchantCategoryFound() {
        String merchantName = "Test Merchant";
        String mcc = "0000"; // MCC not in map

        when(merchantRepository.findByName(merchantName))
                .thenReturn(Optional.of(new Merchant(1L, merchantName, Category.MEAL)));

        Set<Category> categories = categoryService.findCategoriesWithFallback(merchantName, mcc);

        Set<Category> expectedCategories = new LinkedHashSet<>();
        expectedCategories.add(Category.MEAL); // from merchant
        expectedCategories.add(Category.CASH); // fallback

        assertEquals(expectedCategories, categories, "Should return categories from MCC and fallback when merchant not found");
    }

    @Test
    void testFindCategoriesWithFallback_OnlyMccCategoryFound() {
        String merchantName = "Unknown Merchant";
        String mcc = "5411";

        when(merchantRepository.findByName(merchantName))
                .thenReturn(Optional.empty());

        Set<Category> categories = categoryService.findCategoriesWithFallback(merchantName, mcc);

        Set<Category> expectedCategories = new LinkedHashSet<>();
        expectedCategories.add(Category.FOOD); // from mcc
        expectedCategories.add(Category.CASH); // fallback

        assertEquals(expectedCategories, categories, "Should return categories from MCC and fallback when merchant not found");
    }

    @Test
    void testFindCategoriesWithFallback_FallbackOnly() {
        String merchantName = "Unknown Merchant";
        String mcc = "0000"; // MCC not in map

        when(merchantRepository.findByName(merchantName))
                .thenReturn(Optional.empty());

        Set<Category> categories = categoryService.findCategoriesWithFallback(merchantName, mcc);

        Set<Category> expectedCategories = new LinkedHashSet<>();
        expectedCategories.add(Category.CASH); // fallback

        assertEquals(expectedCategories, categories, "Should return only fallback category when neither merchant nor MCC match");
    }
}