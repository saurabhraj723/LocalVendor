package com.localflow.catalogue.service;

import com.localflow.business.repository.BusinessRepository;
import com.localflow.catalogue.api.dto.CategoryRequest;
import com.localflow.catalogue.api.dto.CategoryResponse;
import com.localflow.catalogue.domain.Category;
import com.localflow.catalogue.repository.CategoryRepository;
import com.localflow.catalogue.repository.ProductRepository;
import com.localflow.common.exception.ConflictException;
import com.localflow.common.exception.NotFoundException;
import com.localflow.common.security.CurrentUserProvider;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final ProductRepository productRepository;
    private final BusinessRepository businessRepository;
    private final CurrentUserProvider currentUserProvider;

    public CategoryService(
            CategoryRepository categoryRepository,
            ProductRepository productRepository,
            BusinessRepository businessRepository,
            CurrentUserProvider currentUserProvider
    ) {
        this.categoryRepository = categoryRepository;
        this.productRepository = productRepository;
        this.businessRepository = businessRepository;
        this.currentUserProvider = currentUserProvider;
    }

    @Transactional(readOnly = true)
    public List<CategoryResponse> list() {
        Long businessId = currentUserProvider.currentBusinessId();
        return categoryRepository.findAllByBusiness_IdOrderBySortOrderAscNameAsc(businessId)
                .stream()
                .map(CategoryResponse::from)
                .toList();
    }

    @Transactional
    public CategoryResponse create(CategoryRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        String name = request.name().trim();
        ensureNameAvailable(businessId, name, null);

        Category category = new Category();
        category.setBusiness(businessRepository.getReferenceById(businessId));
        apply(category, request, name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public CategoryResponse update(Long id, CategoryRequest request) {
        Long businessId = currentUserProvider.currentBusinessId();
        Category category = findCategory(businessId, id);
        String name = request.name().trim();
        ensureNameAvailable(businessId, name, id);
        apply(category, request, name);
        return CategoryResponse.from(categoryRepository.save(category));
    }

    @Transactional
    public void delete(Long id) {
        Long businessId = currentUserProvider.currentBusinessId();
        Category category = findCategory(businessId, id);
        if (productRepository.existsByBusiness_IdAndCategory_Id(businessId, id)) {
            throw new ConflictException("CATEGORY_IN_USE", "Category cannot be deleted while products use it.");
        }
        categoryRepository.delete(category);
    }

    private Category findCategory(Long businessId, Long id) {
        return categoryRepository.findByIdAndBusiness_Id(id, businessId)
                .orElseThrow(() -> new NotFoundException("CATEGORY_NOT_FOUND", "Category was not found."));
    }

    private void ensureNameAvailable(Long businessId, String name, Long excludeId) {
        boolean exists = excludeId == null
                ? categoryRepository.existsByBusiness_IdAndNameIgnoreCase(businessId, name)
                : categoryRepository.existsByBusiness_IdAndNameIgnoreCaseAndIdNot(businessId, name, excludeId);
        if (exists) {
            throw new ConflictException("CATEGORY_NAME_ALREADY_EXISTS", "A category with this name already exists.");
        }
    }

    private static void apply(Category category, CategoryRequest request, String name) {
        category.setName(name);
        category.setDescription(request.description() == null || request.description().isBlank()
                ? null
                : request.description().trim());
        category.setSortOrder(request.sortOrder());
    }
}
