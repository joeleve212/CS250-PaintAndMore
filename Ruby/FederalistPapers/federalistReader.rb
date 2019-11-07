# Reading the Federalist Papers 
#
# A Fed object contains one Federalist paper
# (Right now it has only the number)
#
class Fed
    attr_accessor :fedno
    attr_accessor :title
    attr_accessor :pub
    attr_accessor :auth

    # Constructor
    def initialize
      @fedno=0
      @title=""
      @pub=""
      @auth=""

    end

    # Method to print data on one Fed object
    def prt(outputFile)
      # puts "\nFederalist #@fedno" + " Title: #@title" #used for verification before 
      puts "#@fedno Title: #@title\nPublication: #@pub\n\n"                     #exporting to HTML doc
      # puts "Written by: #@auth"
      outputFile.puts "<tr><th>#@fedno</th><th>#@auth</th><th>#@title</th><th>#@pub</th></tr>"
    end
end

# Input will come from file fed.txt
file = File.new("fed.txt", "r")

# Output will go to HTML doc
outFile = File.new("organizedFed.html","w")
outFile.puts "<html>\n<head><title>Federalist Index</title></head>\n<body>\n<h3>Federalist Index</h3>\n<table>"
# List of Fed objects 
feds = []

# Read and process each line
while (line = file.gets)
    line.strip!            # Remove trailing white space
    words = line.split     # Split into array of words
    if words.length == 0 then
        next
    end
    authFound = false
    titleFound = false

    # "FEDERALIST No. number" starts a new Fed object
    if words[0]=="FEDERALIST" then
       curFed = Fed.new    # Construct new Fed object
       feds << curFed      # Add it to the array
       curFed.fedno = words[2]
       while(!authFound)   # Check lines until the Author's name is found
         line = file.gets  # word parsing of the next line
         line.strip!
         words = line.split
         if words.length != 0 && !titleFound then
          titleFound = true   #after any empty lines, the next text is the title
          while(words.length != 0) #iterate through all lines of title & publication
            prevLine = line #temp store the current line text
            if line.index('For') == 0 || line.index('From') == 0 then
              curFed.pub=line              
            end
            line = file.gets #update with next line
            line.strip!
            words = line.split
            # if words.length == 0 && file.gets then #grab next line if this one is blank (for #54)
            #   line = file.gets #update with next line
            #   line.strip!
            #   words = line.split
            # end
            if curFed.fedno == "58" && words.length != 0 then
              curFed.title << " "
              curFed.title << prevLine
              curFed.title << " "
              curFed.title << line #add the previous line to the title
              curFed.pub = "None"
              next
            elsif line.index('From') == 0 && curFed.fedno == "54" then
              curFed.pub = line
              next
            elsif curFed.fedno == "54" then
              curFed.title << prevLine
              curFed.pub = line
              next
            elsif line.index('17') == nil && words.length != 0 then #if this line doesn't include a date
              curFed.pub=line #store the current line as the publication
            elsif (prevLine.index('For') != 0 && prevLine.index('From') != 0) && line.index('17') != nil then #if last line is real pub
              #line = file.gets #skip to next line if this one has a date in it
              curFed.title << " "
              curFed.title << prevLine #add the previous line to the title
            end
#            words = line.split
            if words.length != 0 && prevLine != curFed.pub then #if the next line isn't blank, 
              curFed.title << " "
              curFed.title << prevLine #add the previous line to the title
            end
          end
          # curFed.findPub
          # the current line should be the blank between title & author
         end
         if titleFound then #if statement for verification that title & publication are 
           line = file.gets #already taken care of, so author can be looked for
           line.strip!
           words=line.split
           if words[0] == "From" then
             curFed.pub = line
             line = file.gets
             line = file.gets
             line.strip!
             words=line.split
           end
           curFed.auth = line #use the first word on the line as the author name
           if words[0].length != 0 then #verify that the author has a string stored before exiting
             authFound = true
           end
         end
       end
    end
end # End of reading

file.close

# Apply the prt (print) method to each Fed object in the feds array
feds.each{|f| f.prt(outFile)}
outFile.puts "</table>\n</body>\n</html>" # add closing tags to HTML doc
outFile.close