from rest_framework import serializers
from .models import Serie, Medicamento

class SerieSerializer(serializers.ModelSerializer):
    class Meta:
        model = Serie
        fields = ['id', 'name', 'release_date', 'rating', 'category']
        read_only_fields = ['id']

class MedicamentoSerializer(serializers.ModelSerializer):
    class Meta:
        model = Medicamento
        fields = ['id', 'nombre', 'dosis', 'frecuencia', 'cantidad']
        read_only_fields = ['id']
