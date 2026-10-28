from django.core.management.base import BaseCommand
from catalogo.models import Serie, Medicamento

class Command(BaseCommand):
    help = 'Agrega datos ficticios de demostración sin borrar registros existentes.'
    def handle(self, *args, **options):
        Serie.objects.get_or_create(name='Lost', defaults={'release_date':'2004-09-22','rating':8,'category':'Misterio'})
        Serie.objects.get_or_create(name='Dark', defaults={'release_date':'2017-12-01','rating':9,'category':'Ciencia ficción'})
        Medicamento.objects.get_or_create(nombre='Ejemplo A (demo)', defaults={'dosis':'Texto de prueba','frecuencia':'Horario de prueba','cantidad':20})
        self.stdout.write(self.style.SUCCESS('Datos de demostración disponibles.'))
