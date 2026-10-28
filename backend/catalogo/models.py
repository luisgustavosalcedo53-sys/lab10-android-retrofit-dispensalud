from django.db import models
from django.core.validators import MinValueValidator, MaxValueValidator

class Serie(models.Model):
    name = models.CharField(max_length=120)
    release_date = models.DateField()
    rating = models.IntegerField(validators=[MinValueValidator(0), MaxValueValidator(10)])
    category = models.CharField(max_length=80)

class Medicamento(models.Model):
    nombre = models.CharField(max_length=120)
    dosis = models.CharField(max_length=80)
    frecuencia = models.CharField(max_length=80)
    cantidad = models.PositiveIntegerField(validators=[MaxValueValidator(1000000)])
