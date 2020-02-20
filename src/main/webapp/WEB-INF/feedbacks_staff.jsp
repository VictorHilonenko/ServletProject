<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/WEB-INF/header.jsp" />

	<section>
		<c:if test = "${paginationFeedbacksDTO != null}">
		    <c:if test = "${paginationFeedbacksDTO.totalRecords > 0}">
                <div class="schedule_cont">
                    <c:choose>
                        <c:when test = "${userPrincipal.hasRoleTag('admin')}">
                            <h1><fmt:message key="i18n.allFeedbacks"/></h1>
                        </c:when>
                        <c:otherwise>
                            <h1><fmt:message key="i18n.forYouFeedbacks"/></h1>
                        </c:otherwise>
                    </c:choose>
                    <table class="table_schedule">
                        <tr>
                            <th><fmt:message key="i18n.date"/></th>
                            <th><fmt:message key="i18n.time"/></th>
                            <th><fmt:message key="i18n.serviceType"/></th>
                            <th><fmt:message key="i18n.USER"/></th>
                            <th><fmt:message key="i18n.MASTER"/></th>
                            <th><fmt:message key="i18n.rating"/></th>
                            <th><fmt:message key="i18n.text"/></th>
                        </tr>
                        <c:forEach var="feedbackDTO" items="${paginationFeedbacksDTO.items}">
                            <tr>
                                <td>${feedbackDTO.appointmentDate}</td>
                                <td>${feedbackDTO.appointmentTime}</td>
                                <td><fmt:message key="${feedbackDTO.serviceType}"/></td>
                                <td>${feedbackDTO.customerName}</td>
                                <td>${feedbackDTO.masterName}</td>
                                <td>${feedbackDTO.rating}</td>
                                <td>${feedbackDTO.textMessage}</td>
                            </tr>
        				</c:forEach>
                    </table>

                    <c:if test = "${paginationFeedbacksDTO.totalPages > 1}">
                        <div class="pagination">
                            <c:if test = "${paginationFeedbacksDTO.hasPrevious()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page-1}">&laquo;</a>
                            </c:if>

                            <c:forEach var="i" begin="0" end="${paginationFeedbacksDTO.totalPages - 1}" step="1">
                                <c:choose>
                                    <c:when test = "${paginationFeedbacksDTO.page == i}">
                                        <a class="active" >${i+1}</a>
                                    </c:when>
                                    <c:otherwise>
                                        <a href="/feedbacks?page=${i}">${i+1}</a>
                                    </c:otherwise>
                                </c:choose>
                            </c:forEach>

                            <c:if test = "${paginationFeedbacksDTO.hasNext()}">
                                <a href="/feedbacks?page=${paginationFeedbacksDTO.page+1}">&raquo;</a>
                            </c:if>
                        </div>
                    </c:if>
                </div>
            </c:if>
		    <c:if test = "${paginationFeedbacksDTO.totalRecords == 0}">
                <div class="schedule_cont">
                    <h3><fmt:message key="i18n.nothing_to_show"/></h3>
                </div>
            </c:if>
		</c:if>

    </section>
    <br>
    <br>
    <br>

<jsp:include page="/WEB-INF/footer.jsp" />