from rest_framework.test import APITestCase

class CrudTests(APITestCase):
    def exercise_crud(self, endpoint, payload, changed):
        created = self.client.post(endpoint, payload, format='json')
        self.assertEqual(created.status_code, 201)
        pk = created.data['id']
        detail = f'{endpoint}/{pk}'
        self.assertEqual(self.client.get(detail).data['id'], pk)
        self.assertIsInstance(self.client.get(endpoint).data, list)
        updated = self.client.put(detail, {**payload, **changed}, format='json')
        self.assertEqual(updated.status_code, 200)
        for key, value in changed.items():
            self.assertEqual(self.client.get(detail).data[key], value)
        self.assertEqual(self.client.delete(detail).status_code, 204)
        self.assertEqual(self.client.get(detail).status_code, 404)

    def test_series_crud(self):
        self.exercise_crud('/serie', {'name':'Serie prueba','release_date':'2026-01-20','rating':8,'category':'Drama'}, {'release_date':'2026-02-20'})

    def test_medicamentos_crud(self):
        self.exercise_crud('/medicamento', {'nombre':'Demo','dosis':'Prueba','frecuencia':'Prueba','cantidad':10}, {'cantidad':25})

    def test_invalid_series_are_rejected(self):
        for changes in [{'rating':11}, {'release_date':'2026-02-30'}, {'name':' '}]:
            payload = {'name':'Demo','release_date':'2026-01-20','rating':8,'category':'Drama', **changes}
            self.assertEqual(self.client.post('/serie',payload,format='json').status_code,400)

    def test_negative_stock_is_rejected(self):
        self.assertEqual(self.client.post('/medicamento', {'nombre':'Demo','dosis':'Prueba','frecuencia':'Prueba','cantidad':-1},format='json').status_code,400)

    def test_missing_update_does_not_create(self):
        self.assertEqual(self.client.put('/serie/99999', {'name':'Demo','release_date':'2026-01-20','rating':8,'category':'Drama'},format='json').status_code,404)
