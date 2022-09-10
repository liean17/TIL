# MapStruct
엔티티 <-> Dto변환을 간편하게 해주는 라이브러리.  
>기본 준비물  
1. 대상이 될 객체
2. 대상이 될 Dto
3. 객체의 Mapper 인터페이스

### 사용법
1. 변환 대상이 될 Dto를 생성한다.
2. 객체에 대한 Mapper인터페이스를 생성하고 각 메서드들을 정의한다.
3. 빌드하면 자동으로 Mapper인터페이스의 구현체가 생성되고 이를 통해 매핑할 수 있다.
```java
@Mapper(componentModel = "spring")
public interface MemberMapper {

    MemberMapper INSTANCE = Mappers.getMapper(MemberMapper.class);

    Member memberPostDtoToMember(MemberDto.Post postDto);
    Member memberResponseDtoToMember(MemberDto.Response responseDto);
    Member memberPatchDtoToMember(MemberDto.Patch patchDto);
    default MemberDto.Response memberToMemberResponseDto(Member member){
        if ( member == null ) {
            return null;
        }
        MemberDto.Response response = new MemberDto.Response(member.getUsername(), member.getPassword());
        return response;
    }
}
```
### 주의점
 - Getter와 생성자에 유의해야한다. 매핑하는 메서드가 제대로 생성되지 않을 수 있다.
 - 각 객체마다 인터페이스를 일일이 만들기보다 GeneralMapper를 정의해두면 더욱 편하게 사용할 수 있다.
```java
// D: dto, E: entity
public interface EntityMapper<D, E> {
    E toEntity(D dto);

    D toDto(E entity);

    List<E> toEntity(List<D> dtoList);

    List<D> toDto(List<E> entityList);

    @Named("partialUpdate")
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void partialUpdate(@MappingTarget E entity, D dto);
}
```
 ---

 ### 해결해야할 의문점
 - entity -> dto 는 바로 됐는데 그 반대가 안된다.  
 내가 참조한 코드에서는 이 경우 직접 작성하던데 또 다른 코드에서는 그냥해도 된다.  
 그 차이를 알아야한다.
 - update기능을 제공하는데 이에 대해 좀 더 알아봐야할 것 같다.


