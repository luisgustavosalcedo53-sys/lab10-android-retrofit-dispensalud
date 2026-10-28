from rest_framework import generics
from rest_framework.decorators import api_view
from rest_framework.response import Response
from .models import Serie, Medicamento
from .serializers import SerieSerializer, MedicamentoSerializer

@api_view(['GET'])
def index(request):
    return Response({'mensaje': 'Servidor activo', 'series': '/serie', 'medicamentos': '/medicamento'})

class Series(generics.ListCreateAPIView):
    queryset = Serie.objects.order_by('id')
    serializer_class = SerieSerializer

class SerieDetalle(generics.RetrieveUpdateDestroyAPIView):
    queryset = Serie.objects.all()
    serializer_class = SerieSerializer

class Medicamentos(generics.ListCreateAPIView):
    queryset = Medicamento.objects.order_by('id')
    serializer_class = MedicamentoSerializer

class MedicamentoDetalle(generics.RetrieveUpdateDestroyAPIView):
    queryset = Medicamento.objects.all()
    serializer_class = MedicamentoSerializer
