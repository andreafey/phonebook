package phonebook

class LowerAlphanumTrie[I] extends PrefixTrie[I] {
    /** Helper method to strip off non-alphanumeric chars */
    private def transform(string:String):String = transform(string.toList).mkString
    private def transform(chars:List[Char]):List[Char] = 
        chars filter (c => c.isLetterOrDigit) map (c => c.toLower)

    override def put(string:String, item:I):Unit = super.put(transform(string), item)
    override def find(chars:List[Char]) = super.find(transform(chars))
    override def remove(string:String) = super.remove(transform(string))
}