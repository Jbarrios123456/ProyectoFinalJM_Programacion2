import 'package:flutter/material.dart';

void main() {
  runApp(const MyApp());
}

class MyApp extends StatelessWidget {
  const MyApp({super.key});

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      title: 'Proyeto Final - Junior Barrios y Maynor del Valle',
      theme: ThemeData(
        colorScheme: .fromSeed(seedColor: const Color.fromARGB(255, 3, 119, 104)),
      ),
      home: const MyHomePage(title: 'Proyeto Final - Junior Barrios y Maynor del Valle'),
    );
  }
}

class MyHomePage extends StatefulWidget {
  const MyHomePage({super.key, required this.title});

  final String title;

  @override
  State<MyHomePage> createState() => _MyHomePageState();
}

class _MyHomePageState extends State<MyHomePage> {

  final TextEditingController _tipoController = TextEditingController();
  final TextEditingController _clienteController = TextEditingController();

  @override
  void dispose() {
    _tipoController.dispose();
    _clienteController.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        backgroundColor: Theme.of(context).colorScheme.inversePrimary,
        title: Text(widget.title),
      ),
      body: SingleChildScrollView(
        padding: const EdgeInsets.all(16),
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            const Text(
              'Equipos disponibles por tipo',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _tipoController,
              decoration: const InputDecoration(
                labelText: 'Tipo de equipo',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: () {  },
              child: const Text('Consultar equipos'),
            ),
            const SizedBox(height: 32),

            const Text(
              'Historial y saldo del cliente',
              style: TextStyle(fontSize: 18, fontWeight: FontWeight.bold),
            ),
            const SizedBox(height: 8),
            TextField(
              controller: _clienteController,
              decoration: const InputDecoration(
                labelText: 'Identificador del cliente',
                border: OutlineInputBorder(),
              ),
            ),
            const SizedBox(height: 8),
            ElevatedButton(
              onPressed: () {  },
              child: const Text('Consultar cliente'),
            ),

            const SizedBox(height: 32),
          ],
        ),
      ),
    );
  }
}
