from django.urls import path
from catalogo import views
urlpatterns = [
 path('', views.index),
 path('serie', views.Series.as_view()),
 path('serie/<int:pk>', views.SerieDetalle.as_view()),
 path('medicamento', views.Medicamentos.as_view()),
 path('medicamento/<int:pk>', views.MedicamentoDetalle.as_view()),
]
