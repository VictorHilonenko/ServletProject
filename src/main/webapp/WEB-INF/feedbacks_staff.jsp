<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt"%>

<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="messages"/>

<jsp:include page="/WEB-INF/header.jsp" />

	<section>
		<h1>Feedbacks</h1>
		<c:if test = "${paginationFeedbacksDTO != null}">
		    <c:if test = "${paginationFeedbacksDTO.totalRecords > 0}">
                <div class="schedule_cont">
                    <c:choose>
                        <c:when test = "${userPrincipal.hasRoleTag('admin')}">
                            <h3>All feedbacks</h3>
                        </c:when>
                        <c:otherwise>
                            <h3>Feedbacks for you</h3>
                        </c:otherwise>
                    </c:choose>
                    <table class="table_schedule">
                        <tr>
                            <th>Date</th>
                            <th>Time</th>
                            <th>Service type</th>
                            <th>Customer</th>
                            <th>Master</th>
                            <th>Rating</th>
                            <th>Text</th>
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
                    <h3>Nothing to show yet...</h3>
                </div>
            </c:if>
		</c:if>

    </section>

<jsp:include page="/WEB-INF/footer.jsp" />