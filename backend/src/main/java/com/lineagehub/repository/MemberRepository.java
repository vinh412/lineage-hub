package com.lineagehub.repository;

import com.lineagehub.entity.Member;
import com.lineagehub.entity.enums.Gender;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
    
    Page<Member> findByFullNameContainingIgnoreCase(String fullName, Pageable pageable);
    
    Page<Member> findByGeneration(Integer generation, Pageable pageable);
    
    Page<Member> findByGender(Gender gender, Pageable pageable);
    
    Page<Member> findByIsBloodRelative(Boolean isBloodRelative, Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE " +
           "(:search IS NULL OR :search = '' OR LOWER(m.fullName) LIKE LOWER(CONCAT('%', :search, '%'))) AND " +
           "(:generation IS NULL OR m.generation = :generation) AND " +
           "(:gender IS NULL OR m.gender = :gender) AND " +
           "(:isBloodRelative IS NULL OR m.isBloodRelative = :isBloodRelative) AND " +
           "(:isDeceased IS NULL OR (CASE WHEN m.deathDate IS NOT NULL THEN true ELSE false END) = :isDeceased)")
    Page<Member> findByFilters(@Param("search") String search,
                                @Param("generation") Integer generation,
                                @Param("gender") Gender gender,
                                @Param("isBloodRelative") Boolean isBloodRelative,
                                @Param("isDeceased") Boolean isDeceased,
                                Pageable pageable);
    
    @Query("SELECT m FROM Member m WHERE m.id IN :ids")
    List<Member> findAllByIds(@Param("ids") Set<UUID> ids);
    
    // Full-text search query
    @Query(value = "SELECT * FROM members m WHERE " +
           "to_tsvector('simple', m.full_name || ' ' || COALESCE(m.branch_name, '') || ' ' || COALESCE(m.notes, '')) " +
           "@@ plainto_tsquery('simple', :searchTerm)",
           nativeQuery = true)
    List<Member> fullTextSearch(@Param("searchTerm") String searchTerm);
    
    // Count members
    long countByIsBloodRelative(Boolean isBloodRelative);
    
    long countByDeathDateIsNull();
    
    long countByDeathDateIsNotNull();
}
