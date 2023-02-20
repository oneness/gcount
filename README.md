## Generate default chart
   ```
   git clone https://github.com/oneness/gcount.git ~/gcount && make -C ~/gcount gcount
```

## Generate chart with search term(s)
   ```
   clj -X:gcount 0 '"term 0"' 1 '"term 1"' 2 '"term 2"'
```